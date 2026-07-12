# 암호화 채팅 시스템

비밀번호 기반 인증과 종단간 암호화(E2E Encryption)를 적용한 채팅 서비스 설계 문서입니다.

## 개요

사용자 인증은 비밀번호 해시와 전자서명 방식을 결합해 처리하고, 채팅 메시지는 대칭키 공유(ECDH) 방식을 통해 종단간 암호화됩니다. 개인키는 항상 암호화된 상태로 저장되며, 로그인 시 비밀번호로 복호화되어 서명 생성에만 사용됩니다.

## 데이터 모델

### User

| 필드 | 설명 |
|---|---|
| `userId` | 사용자 ID |
| `password` | 사용자 비밀번호 |
| `userName` | 사용자 이름 |
| `publicKey` | 인증용 공개키 |
| `encryptedPrivateKey` | 인증용 개인키 (암호화 저장) |
| `chatPublicKey` | 채팅용 공개키 |
| `encryptedChatPrivateKey` | 채팅용 개인키 (암호화 저장) |

**UserRepository 저장 규칙** (`users.json`)
- `userId`, `userName`, `publicKey`, `chatPublicKey` → 평문 그대로 저장
- `password` → 해시값으로 저장
- `encryptedPrivateKey`, `encryptedChatPrivateKey` → 암호문으로 저장

### Chat

| 필드 | 설명 |
|---|---|
| `encryptedMessage` | 암호화된 메시지 |
| `senderId` | 보내는 사람 ID |
| `receiverId` | 받는 사람 ID |

**ChatRepository 저장 규칙** (`message.json`)
- `senderId`, `receiverId` → 평문 그대로 저장
- `encryptedMessage` → 암호문으로 저장

## 보안 유틸리티

### HashUtil
- 알고리즘: `SHA-256`
- 해시값에 대해 1000회 반복 계산 (키 스트레칭)
- 회원가입 시 비밀번호 해시값 생성에 사용

### CipherUtil
- 알고리즘: `AES-GCM`
- `IV`: 12바이트
- `SEED`: 16바이트
- `encrypt(평문, 키)`: SEED값과 평문을 결합해 암호화
- `decrypt(암호문, 키)`: 키로 암호문 복호화

### ChatKeyPair
- 알고리즘: `EC` (타원곡선 암호)
- `generateKeyPair()`: 채팅용 키 쌍 생성
- `sharedSecret(개인키, 공유키)`: 채팅용 공유키(ECDH) 계산

### DigitalSign
- `generateChallenge(길이)`: 로그인용 challenge 값 생성
- `generateSignature(개인키, challenge)`: 전자서명 생성
- `verifySignature(공개키, challenge, 서명값)`: 전자서명 검증

## 서비스 로직

### UserService

**회원가입 — `joinMember()`**
1. 회원 ID, 비밀번호 입력
2. 비밀번호에 대한 해시값 생성 (`HashUtil`)
3. 로그인용 키 쌍 생성 → 개인키는 비밀번호 해시값으로 암호화, 공개키는 그대로 저장
4. 채팅용 키 쌍 생성 → 개인키는 비밀번호 해시값으로 암호화, 공개키는 그대로 저장

**로그인 — `login()`**
1. 회원 ID 입력 → 존재하는 회원인지 확인
2. 비밀번호 입력 → 저장된 개인키 복호화
3. 복호화된 개인키로 전자서명값 생성
4. 공개키로 전자서명 검증 → 인증 완료

### ChatService

**메시지 보내기 — `sendChat(보내는 사람 ID, 받는 사람 ID, 비밀번호)`**
1. 송신자/수신자 ID가 `UserRepository`에 존재하는지 확인
2. 채팅용 개인키·공개키를 가져와 공유키(shared secret) 생성
3. 메시지를 공유키로 암호화 후 저장

**메시지 받기 — `getChat(받는 사람 ID, 보내는 사람 ID, 비밀번호)`**
1. 수신자/송신자 ID가 `UserRepository`에 존재하는지 확인
2. 채팅용 개인키·공개키를 가져와 공유키(shared secret) 생성
3. 공유키로 메시지 복호화 후 읽기

## 보안 설계 요약

- 비밀번호는 평문 저장 없이 SHA-256 + 1000회 반복 해시로만 저장
- 개인키는 항상 AES-GCM 암호문 형태로 저장되며, 비밀번호 해시값을 복호화 키로 사용
- 로그인 인증은 비밀번호 자체가 아닌 **전자서명 challenge-response** 방식으로 처리해 재사용 공격을 방지
- 채팅 메시지는 EC 기반 키 교환(ECDH)으로 생성한 공유키를 사용해 종단간 암호화
