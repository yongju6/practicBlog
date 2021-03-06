package com.korea.dbapp.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.korea.dbapp.domain.comment.Comment;
import com.korea.dbapp.domain.comment.CommentRepository;
import com.korea.dbapp.domain.post.Post;
import com.korea.dbapp.domain.post.PostRepository;
import com.korea.dbapp.domain.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 필드가 붙어 있는 애들을 생성자에 집어넣어서 만들어줌
@Controller
public class PostController {

	private final PostRepository postRepository;
	private final HttpSession session;
	private final CommentRepository commentRepository;
	// final은 무조건 생성자를 만들어주어야함

	@GetMapping({ "/", "/post" }) // 주소가 2개!
	public String list(Model model, Integer page) { // model = request
		if (page == null) {
			page = 0;
		}
		// 핵심 로직
		// post 여러건을 db에서 들고옴
		model.addAttribute("postsEntity", postRepository.findAll(PageRequest.of(page, 4))); // 아무것도 안나옴 => 이유는 content안에 있기
																																												// 때문에!
																																												// 실행하면 page값이 없어서 화면이 뜨지 않음
																																												// page는 데이터로 넘어갈 때 page 타입으로
																																												// 넘어감

		// SELECT * FROM post LIMIT 0, 3; -> 0~3까지 POST 데이터 가져옴
		// 쿼리문을 짜고 동적으로 바껴야하는데 SPRING에서는 PageRequest로 해줌
		// SELECT MAX(ID) FROM post; => max 값을 알려줌 : 마지막 페이지라는 것을 알 수 있음 >> 페이지 넘기기 버튼
		// 안눌리게
		// 중간에 삭제되면 max값이 정확하지 않기 때문에
		// SELECT COUNT(*) FROM post; => 갯수를 알려줌 : 마지막 페이지를 정확하게 알 수 있음
		// spring에서는 마지막 페이지도 계산해서 줌 (last : true 로 반환해줌)

		return "post/list"; // ViewResolver의 도움 + RequestDispatcher ( request 유지 기법)
	}

	@GetMapping("/post/{id}")
	public String detail(@PathVariable int id, Model model) {
		Post postEntity = postRepository.findById(id).get();
		model.addAttribute("postEntity", postEntity);

		// 3. findByAllPostId - Get - 여기서 하는게 아니라 상세보기 페이지를 갈 때 사용해야함 -->
		// PostController에 만들어야함

		List<Comment> commentsEntity = commentRepository.mFindAllByPostId(id);
		model.addAttribute("commentsEntity", commentsEntity);

		return "post/detail";
	}

	@DeleteMapping("/post/{id}")
	public @ResponseBody String deleteById(@PathVariable int id) {
		// 1. 권한 체크(post id를 통해 user id를 찾아서 session의 id 비교) - 생략

		// session의 user id 찾기(session 접근)

		// post의 user id 찾기 ({id})

		// 2. {id} 값으로 삭제
		Post postEntity = postRepository.findById(id).get(); // 어떻게 처리?
		int postUserId = postEntity.getUser().getId();
		User userEntity = (User) session.getAttribute("principal");
		// int sessionUserId =((User)session.getAttribute("principal")).getId();
		int userId = userEntity.getId();
		if (postUserId == userId) {
			postRepository.deleteById(id);
			return "ok";
		} else {
			return "fail";
		} // end if-else
	}

	@GetMapping("/post/saveForm")
	public String saveForm() {
		// 1. 인증 체크

		return "post/saveForm"; // 파일 호출
	}

	// @CrossOrigin
	@PostMapping("/post")
	public String save(Post post) {

		// principal에 password는 없는데 필요한건 id 값이기때문에 id값 이외에는 다 null이어도 무방
		User principal = (User) session.getAttribute("principal");

		if (principal == null) {
			return "redirect:/auth/loginForm"; // 주소 호출
		}
		post.setUser(principal);
		postRepository.save(post);
		return "redirect:/";
	}

	@GetMapping("/post/{id}/updateForm")
	public String updateForm(@PathVariable int id, Model model) {
		User principal = (User) session.getAttribute("principal");
		int loginId = principal.getId(); // 로그인 한 사람의 아이디

		Post postEntity = postRepository.findById(id).get();
		int postOwnerId = postEntity.getUser().getId();

		if (loginId == postOwnerId) {
			model.addAttribute("postEntity", postEntity);
			return "post/updateForm";
		} else {
			return "redirect:/auth/loginFrom";
		} // end if
	}

	@PutMapping("/post/{id}")
	public @ResponseBody String update(@PathVariable int id, @RequestBody Post post) {
		User user = (User) session.getAttribute("principal");
		int loginId = user.getId();

		Post postEntity = postRepository.findById(id).get();
		int postOwnerId = postEntity.getUser().getId();

		if (loginId == postOwnerId) {
			postEntity.setTitle(post.getTitle());
			postEntity.setContent(post.getContent());
			postRepository.save(postEntity);
			return "ok"; // fecth 요청은 데이터가 return 되어야함
		} else {
			return "fail";
		}
	}

}
