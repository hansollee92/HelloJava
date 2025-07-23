package com.yedam.board;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		boolean run = true;

		System.out.println("      ╔════════════════════════════════════════════╗");
		System.out.println("                  📂 대구시청 알림정보 게시판               ");
		System.out.println();
		System.out.println("            해당하는 번호를 입력하면 글을 확인할 수 있습니다!     ");
		System.out.println("      ╚════════════════════════════════════════════╝");

		while (run) {
			menu();
			int guestSelect = Integer.parseInt(sc.nextLine().trim());
			switch (guestSelect) {
			case 1: // 글목록
				postList();
				break;

			case 2: // 글확인
				postView();
				break;

			case 3: // 글검색
				postSearch();
				break;

			case 4: // 로그인
				postLogin();
				break;

			default: // 종료
				run = false;
			}
		}
		System.out.println("👋 프로그램을 종료합니다.");

	}// end of Main Method

	// 메소드
	// -----------------------------------------------------------------------------------------------------------------------

	// 기본메뉴
	public static void menu() {
		System.out.println();
		System.out.println("―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
		System.out.println("          1.목록 | 2.글확인 | 3.검색 | 4.로그인 | 0.종료");
		System.out.println("―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
		System.out.print("> 번호 선택: ");
	}

	// 글목록
	public static void postList() {
		PostDAO pdao = new PostDAO();
		ArrayList<Post> list = pdao.findAll();
		System.out.println();
		System.out.println("📋 《《 게시글 목록 》》");
		System.out.printf("%-5s %-30s %-8s %-10s\n", "번호", "제목", "글쓴이", "작성일");
		System.out.println("--------------------------------------------------------------");
		for (int i = 0; i < list.size(); i++) {
			System.out.printf("%-5d  %-30s %-5s %-10s\n", list.get(i).getBoardNo(), list.get(i).getTitle(),
					list.get(i).getMember().getUserName(), list.get(i).getRedate());
		}
		System.out.println();
	}

	// 글확인 & 댓글확인 (댓글 작성)
	public static void postView() {
		Scanner sc = new Scanner(System.in);
		PostDAO pdao = new PostDAO();
		CommentsDAO cdao = new CommentsDAO();
		System.out.print("> 글번호 입력: ");
		int bno = Integer.parseInt(sc.nextLine());
		Post postCheck = pdao.postCheck(bno);
		ArrayList<Comments> commentsList = cdao.findComments(bno);

		if (postCheck != null) {
			System.out.println();
			System.out.println("📋 《《 게시글 확인 》》");
			System.out.printf("· 번호 : %d\n", postCheck.getBoardNo());
			System.out.printf("· 제목 : %s\n", postCheck.getTitle());
			System.out.printf("· 글쓴이 : %s\n", postCheck.getMember().getUserName());
			System.out.printf("· 작성일 : %s\n", postCheck.getRedate());
			System.out.println("-----------------------------------------------------------");
			System.out.println(postCheck.getContent());
			System.out.println();
			System.out.println("📬 댓글");

			// 댓글 보기
			if (commentsList.size() == 0) { // 댓글이 없으면
				System.out.println("댓글이 아직 없습니다.");
			} else { // 댓글이 있으면
				for (int i = 0; i < commentsList.size(); i++) {
					System.out.println("-[" + commentsList.get(i).getCommentsNo() + "] "
							+ commentsList.get(i).getNickName() + " : " + commentsList.get(i).getMessage() + " ("
							+ commentsList.get(i).getRedate() + ")");
				}
			}
			System.out.println("-----------------------------------------------------------");

		} else {
			System.out.println("해당하는 글이 없습니다.");
		}

		// 댓글 작성
		System.out.printf("> 댓글을 작성하시겠습니까? (y/n): ");
		String commentCheck = sc.nextLine().trim();

		if (commentCheck.equalsIgnoreCase("y")) {
			Comments comment = new Comments();

			if (LoginContext.loginUser != null) {
				// 로그인 유저 댓글
				comment.setNickName(LoginContext.loginUser.getUserName());
			} else {
				// 게스트 댓글
				System.out.print("> 닉네임: ");
				comment.setNickName(sc.nextLine().trim());
			}
			System.out.print("> 내용: ");
			comment.setMessage(sc.nextLine().trim());
			comment.setBoardNo(bno);

			boolean commentResult = cdao.commentUpload(comment);
			if (commentResult) {
				System.out.println("> 댓글이 등록되었습니다.");
			} else {
				System.out.println("> 댓글 등록에 실패했습니다.");
			}

		}

	}

	// 검색
	public static void postSearch() {

		Scanner sc = new Scanner(System.in);
		PostDAO pdao = new PostDAO();

		while (true) {
			System.out.println();
			System.out.println("📋 《《 게시글 검색 》》");
			System.out.println("검색할 항목을 아래에서 입력해 주세요.");
			System.out.println("⋯ 1. 제목");
			System.out.println("⋯ 2. 글쓴이");
			System.out.println("-----------------------------------------------------------");
			System.out.print("> ");

			int srhNo;
			// 바로 숫자변환하면 에러터짐 (사용자가 문자열값을 넣을 경우 형변환 불가능)

			// 일단 선언 한 다음에 try-catch문으로 처리
			// NumberFormatException : 예외클래스로 숫자가 아닌 것들을 강제형변환할때 일어나는 에러
			// 이 에러가 일어나면~ 아래를 출력해라라는 의미!
			try {
				srhNo = Integer.parseInt(sc.nextLine().trim());
			} catch (NumberFormatException e) { // 숫자가 아닌 값 들어왔을 때
				System.out.println("1 또는 2 를 입력해주세요!");
				continue;
			}

			String column = null;
			String keyword = null;
			if (srhNo == 1) {
				column = "p.title";
				System.out.print("> 검색어를 입력하세요: ");
				keyword = sc.nextLine().trim();
			} else if (srhNo == 2) {
				column = "m.userName";
				System.out.print("> 글쓴이를 입력하세요: ");
				keyword = sc.nextLine().trim();
			} else { // 1,2가 아닌 숫자값이 들어왔을 때
				System.out.println("1 또는 2 를 입력해주세요");
				continue;
			}

			ArrayList<Post> result = pdao.postSearch(column, keyword);
			if (keyword.isBlank()) { // == keyword.trim().isEmpty()
				System.out.println("아무것도 입력이 되지 않았습니다. 값을 입력해주세요!");
				continue;
			}
			if (!result.isEmpty()) {
				System.out.println();
				System.out.println("📋 《《 게시글 검색 결과 》》");
				System.out.printf("%-5s %-30s %-8s %-10s\n", "번호", "제목", "글쓴이", "작성일");
				System.out.println("--------------------------------------------------------------");
				for (Post post : result) {
					System.out.printf("%-5d %-30s %-8s %-10s\n", post.getBoardNo(), post.getTitle(),
							post.getMember().getUserName(), post.getRedate());
				}
				System.out.println();
			} else {
				System.out.println("해당 키워드가 검색되지 않습니다.");
			}
			break;

		}
	}

	// 로그인
	public static void postLogin() {

		Scanner sc = new Scanner(System.in);
		MemberDAO mdao = new MemberDAO();

		System.out.println();
		System.out.println("🔓 《《 로그인 》》");
		System.out.print("> ID: ");
		int id = Integer.parseInt(sc.nextLine());
		System.out.print("> PW: ");
		String pw = sc.nextLine();

		Member login = mdao.login(id, pw);
		if (login != null) {
			LoginContext.loginUser = login; // 로그인 사용자 정보 저장 -> adminMenu()에서 사용가능
			System.out.println();
			System.out.print("😎🎉'" + login.getUserName() + "'님 안녕하세요!\n");
			adminMenu();
		} else {
			System.out.println("ID 또는 비밀번호가 일치하지 않습니다.\\n");
		}

	}

	// 메뉴 (로그인후)
	public static void adminMenu() {
		Scanner sc = new Scanner(System.in);
		boolean userRun = true;
		while (userRun) {
			System.out.println("―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
			System.out.println("1.목록 | 2.글확인 | 3.검색 | 4.글등록 | 5.글수정 | 6.글삭제 | 7.로그아웃");
			System.out.println("―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
			System.out.print("> 번호 선택: ");
			int userSelect = Integer.parseInt(sc.nextLine());

			switch (userSelect) {
			case 1: // 글목록
				postList();
				break;

			case 2: // 글확인
				postView();
				break;

			case 3: // 검색
				postSearch();
				break;

			case 4: // 글등록
				postAdd();
				break;

			case 5: // 글수정
				postEdit();
				break;

			case 6: // 글삭제
				postdel();
				break;

			default: // 로그아웃
				userRun = false;
				System.out.println();
			}
		} // end while.

	}

	// 글 등록
	public static void postAdd() {
		Scanner sc = new Scanner(System.in);

		System.out.print("> 제목: ");
		String title = sc.nextLine();
		System.out.println("> 내용: ");
		String content = sc.nextLine();

		// Post 객체 생성
		Post post = new Post(title, content);

		// PostDAO의 postUpload()를 이용해 글 등록
		PostDAO pdao = new PostDAO();
		boolean postResult = pdao.postUpload(post);
		if (postResult) {
			System.out.println("글이 정상으로 등록되었습니다.");
		} else {
			System.out.println("글 등록 중 오류가 발생했습니다.");
		}
	}

	// 글수정
	public static void postEdit() {
		Scanner sc = new Scanner(System.in);

		System.out.printf("> 수정할 글번호 선택: ");
		int boardNo = Integer.parseInt(sc.nextLine());

		PostDAO pdao = new PostDAO();
		Post post = pdao.findPost(boardNo);

		// 글이 존재하는지 아닌지 확인
		if (post == null) {
			System.out.println("존재하지 않는 글입니다.");
			return;
		}

		// 본인이 작성한 글인지 아닌지 확인
		int postId = post.getId(); // 글작성자 유저 id
		int currentId = LoginContext.loginUser.getId(); // 현재 로그인된 유저 id

		if (postId == currentId) {
			System.out.println("'" + post.getTitle() + "' 글을 수정합니다.");

			System.out.print("> 제목: ");
			String newTitle = sc.nextLine();
			System.out.println("> 내용: ");
			String newContent = sc.nextLine();

			// post 객체 생성
			Post updatePost = new Post(boardNo, newTitle, newContent);

			if (pdao.postUpdate(updatePost)) {
				System.out.println("글이 정상적으로 수정되었습니다.");
			} else {
				System.out.println("글 수정 중 오류가 발생했습니다.");
			}
		} else {
			System.out.println("다른 사용자의 글은 수정할 수 없습니다.");
		}
	}

	// 글 삭제
	public static void postdel() {
		Scanner sc = new Scanner(System.in);

		System.out.print("> 삭제할 글번호 입력: ");
		int boardNo = Integer.parseInt(sc.nextLine());

		PostDAO pdao = new PostDAO();
		Post post = pdao.findPost(boardNo);

		// 글이 존재하는지 아닌지 확인
		if (post == null) {
			System.out.println("존재하지 않는 글입니다.\n");
			return;
		}

		// 본인 글이 아닌지 맞는지 확인
		int postId = post.getId();
		int currentId = LoginContext.loginUser.getId();

		if (postId != currentId) {
			System.out.println("다른 사용자의 글은 삭제할 수 없습니다.\n");
			return;
		}

		// 삭제여부 확인
		boolean stayMenu = true;
		while (stayMenu) {
			System.out.println("'" + post.getTitle() + "' 글을 삭제하시겠습니까? (y/n)");
			System.out.print("> ");
			String delCheck = sc.nextLine().trim(); // 공백입력시 자동제거

			if (delCheck.equalsIgnoreCase("y")) { // 대소문자구분x를 위해서 equalsIgnoresCase 사용
				boolean delete = pdao.postDel(boardNo);
				if (delete) {
					System.out.println("해당글이 삭제되었습니다.\n");
					stayMenu = false;
				} else {
					System.out.println("글 삭제 중 오류가 발생했습니다. 다시 시도해 주세요.\n");
					stayMenu = false;
				}
			} else if (delCheck.equalsIgnoreCase("n")) {
				System.out.println("삭제를 취소하였습니다.\n");
				stayMenu = false;
			} else {
				System.out.println("y 또는 n을 선택해주세요!");
			}
		}
	}

}// end class
