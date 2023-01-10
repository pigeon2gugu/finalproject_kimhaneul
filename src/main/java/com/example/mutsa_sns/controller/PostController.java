package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.dto.*;
import com.example.mutsa_sns.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ApiOperation(value = "포스트 작성")
    public Response<PostResponse> createPost(@RequestBody PostCreateRequest req, @ApiIgnore Authentication authentication) {
        String userName = authentication.getName();
        PostDto postDto = postService.createPost(req, userName);
        return Response.success(new PostResponse("포스트 등록 완료", postDto.getId()));
    }

    @GetMapping("/{postId}")
    @ApiOperation(value = "1개 포스트 조회")
    public Response<PostDto> getPostDetail(@PathVariable Integer postId) {
        PostDto postDto = postService.detailPost(postId);
        return Response.success(postDto);
    }

    @GetMapping()
    @ApiOperation(value = "전체 포스트 조회")
    public Response<Page<PostDto>> getPost(
            //한페이지 20개의 글, 최신글 정렬
            @PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto> postDtos = postService.getPostAll(pageable);
        return Response.success(postDtos);

    }

    @DeleteMapping("/{postId}")
    @ApiOperation(value = "포스트 삭제")
    public Response<PostResponse> deletePost(@PathVariable Integer postId, @ApiIgnore Authentication authentication) {

        postService.deletePost(authentication.getName(), postId);
        return Response.success(new PostResponse("포스트 삭제 완료", postId));

    }

    @PutMapping("/{postId}")
    @ApiOperation(value = "포스트 수정")
    public Response<PostResponse> modifyPost(@PathVariable Integer postId, @RequestBody PostModifyRequest req, @ApiIgnore Authentication authentication) {

        PostDto postDto = postService.modifyPost(postId, req.getTitle(), req.getBody(), authentication.getName());
        return Response.success(new PostResponse("포스트 수정 완료", postId));
    }

    //댓글 기능
    @PostMapping("/{postId}/comments")
    @ApiOperation(value = "댓글 작성")
    public Response<CommentDto> createComment(@PathVariable Integer postId, @RequestBody CommentRequest req, @ApiIgnore Authentication authentication) {

        CommentDto commentDto = postService.createComment(postId, authentication.getName(), req.getComment());
        return Response.success(commentDto);

    }

    @GetMapping("/{postId}/comments")
    @ApiOperation(value = "댓글 조회")
    public Response<Page<CommentDto>> getComment(@PathVariable Integer postId,
                                                 @PageableDefault(size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CommentDto> commentDtos = postService.getComment(postId, pageable);
        return Response.success(commentDtos);

    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @ApiOperation(value = "댓글 삭제")
    public Response<CommentDeleteResponse> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId, @ApiIgnore Authentication authentication) {

        postService.deleteComment(authentication.getName(), postId, commentId);
        return Response.success(new CommentDeleteResponse("댓글 삭제 완료", commentId));
    }

    @PutMapping("/{postId}/comments/{commentId}")
    @ApiOperation(value = "댓글 수정")
    public Response<CommentModifyResponse> modifyComment(@PathVariable Integer postId, @PathVariable Integer commentId, @RequestBody CommentRequest req, @ApiIgnore Authentication authentication) {

        CommentModifyResponse commentModifyResponse = postService.modifyComment(authentication.getName(), postId, commentId, req.getComment());
        return Response.success(commentModifyResponse);

    }

    //좋아요
    @PostMapping("/{postId}/likes")
    @ApiOperation(value = "좋아요 누르기/취소")
    public Response<String> doLike(@PathVariable Integer postId, @ApiIgnore Authentication authentication) {
        String result = postService.doLike(postId, authentication.getName());
        return Response.success(result);
    }

    @GetMapping("/{postId}/likes")
    @ApiOperation(value = "좋아요 갯수 조회")
    public Response<Integer> getLike(@PathVariable Integer postId) {
        Integer result = postService.getLike(postId);
        return Response.success(result);
    }

    //마이 피드
    @GetMapping("/my")
    @ApiOperation(value = "마이피드")
    public Response<Page<PostDto>> getMyFeed(@PageableDefault(size=20, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                             @ApiIgnore Authentication authentication) {
        Page<PostDto> postDtos = postService.getMyFeed(pageable, authentication.getName());
        return Response.success(postDtos);
    }
}
