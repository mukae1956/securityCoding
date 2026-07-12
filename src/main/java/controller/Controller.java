package controller;

import Service.ChatService;
import Service.UserService;
import domain.User;

import java.util.Scanner;

public class Controller {
    Scanner s = new Scanner(System.in);
    ChatService cs = new ChatService();
    UserService us = new UserService();

    public void run() throws Exception {
        System.out.println("비밀 대화방에 오신 걸 환영합니다🤐");
        System.out.println("필요하신 기능을 선택해 주세요!");
        while(true){
            System.out.println("[1] 회원가입 | [2] 로그인  | [3] 로그아웃 | [4] 종료");
            System.out.println("입력 > ");
            int choice = s.nextInt();
            if (choice == 1 ){
                us.joinMember();
            }
            else if (choice == 2) {
                us.login();
                while(true){
                    System.out.println("필요하신 기능을 선택해 주세요!");
                    System.out.println("[1] 비밀 메시지 보내기🔐 | [2] 비밀 메시지 받기 🔑 | [3] 종료");
                    int choice2 = s.nextInt();
                    s.nextLine();
                    if (choice2 == 1) {
                        System.out.println("비밀 메시지 보내기를 선택하셨습니다!");

                        //로그인 한 상대만 메시지를 보낼 수 있는 조건
                        User loginUser = us.getCurrentUser();
                        if (loginUser == null) {
                            System.out.println("로그인이 필요합니다!");
                            continue;
                        }
                        String userId = loginUser.getUserId();
                        System.out.println("메시지를 보낼 상대방 아이디 입력 > ");
                        String otherId = s.nextLine();
                        System.out.println("내 비밀번호 입력 > ");
                        String pwd = s.nextLine();
                        cs.sendChat(userId, otherId, pwd);
                        System.out.println("메시지를 성공적으로 보냈습니다!");
                    }
                    else if (choice2 == 2) {
                        System.out.println("비밀 메시지 받기를 선택하셨습니다!");
                        //로그인한 회원만 자신의 메시지를 볼 수 있게
                        User loginUser = us.getCurrentUser();
                        if (loginUser == null) {
                            System.out.println("로그인이 필요합니다!");
                            continue;
                        }
                        String userId = loginUser.getUserId();
                        System.out.println("메시지를 받을 상대방 아이디 입력 > ");
                        String otherId = s.nextLine();
                        System.out.println("내 비밀번호 입력 > ");
                        String pwd = s.nextLine();
                        cs.getChat(userId, otherId, pwd);
                    }
                    else if (choice2 == 3) {
                        System.out.println("메시지 기능을 종료합니다!");
                        break;
                    }
                    else {
                        System.out.println("없는 기능입니다! 다시 선택해 주세요");
                    }
                }
            }
            else if (choice == 3) {
                us.logout();
            }
            else if (choice == 4) {
                System.out.println("비밀 대화방을 종료합니다");
                break;
            }
            else {
                System.out.println("없는 기능입니다! 다시 선택해 주세요");
            }

        }
    }
}
