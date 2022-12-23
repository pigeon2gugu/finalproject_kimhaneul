package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.dto.PostCreateRequest;
import com.example.mutsa_sns.domain.dto.PostCreateResponse;
import com.example.mutsa_sns.domain.dto.PostDto;
import com.example.mutsa_sns.domain.dto.Response;
import com.example.mutsa_sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<PostCreateResponse> createPost(@RequestBody PostCreateRequest req, Authentication authentication) {
        String userName = authentication.getName();
        PostDto postDto = postService.createPost(req, userName);
        return Response.success(new PostCreateResponse("포스트 등록 완료", postDto.getId()));
    }

    @GetMapping("/{postId}")
    public Response<PostDto> getPostDetail(@PathVariable Integer postId) {
        PostDto postDto = postService.detailPost(postId);
        return Response.success(postDto);
    }

    @GetMapping()
    public Response<Page<PostDto>> getPost(
            //한페이지 20개의 글, 최신글 정렬
            @PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto> postDtos = postService.getPostAll(pageable);
        return Response.success(postDtos);

    }


}
