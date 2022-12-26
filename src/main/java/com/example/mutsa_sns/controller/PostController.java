package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.dto.*;
import com.example.mutsa_sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<PostResponse> createPost(@RequestBody PostCreateRequest req, Authentication authentication) {
        String userName = authentication.getName();
        PostDto postDto = postService.createPost(req, userName);
        return Response.success(new PostResponse("포스트 등록 완료", postDto.getId()));
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

    @DeleteMapping("/{postId}")
    public Response<PostResponse> deletePost(@PathVariable Integer postId, Authentication authentication) {

        postService.deletePost(authentication.getName(), postId);
        return Response.success(new PostResponse("포스트 삭제 완료", postId));

    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modifyPost(@PathVariable Integer postId, @RequestBody PostModifyRequest req, Authentication authentication) {

        PostDto postDto = postService.modifyPost(postId, req.getTitle(), req.getBody(), authentication.getName());
        return Response.success(new PostResponse("포스트 수정 완료", postDto.getId()));
    }


}
