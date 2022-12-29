<div align=center>
 <h2>MutsaSNS</h2>  
</div>

## :ocean: 프로젝트 소개
**MutsaSNS** : Mutsa-SNS로, 회원가입, 로그인, 글쓰기, 댓글, 좋아요, 알림 기능을 갖고 있는 SNS  

## :milky_way: 개발 기간
+ 2022-12-19 ~  

## :globe_with_meridians: 개발 환경
+ 에디터 : Intellij Ultimate
+ 개발 툴 : SpringBoot 2.7.5
+ 자바 : JAVA 11
+ 빌드 : Gradle 6.8
+ 서버 : AWS EC2
+ 배포 : Docker
+ 데이터베이스 : MySql 8.0

## :dolphin: Swagger
http://ec2-3-35-225-29.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/

## :blue_book: EndPoint
+ **User**
    - **회원 가입**  
        Post /api/v1/users/join  
        ex) http://ec2-3-35-225-29.ap-northeast-2.compute.amazonaws.com:8080/api/v1/users/join  
    - **로그인**  
        Post /api/v1/users/login  
        ex) http://ec2-3-35-225-29.ap-northeast-2.compute.amazonaws.com:8080/api/v1/users/login  
    - **ADMIN 역할 부여**  
        Get /api/v1/users/{userId}/role/change  
        ex) http://ec2-3-35-225-29.ap-northeast-2.compute.amazonaws.com:8080/api/v1/users/2/role/change  
+ **Post**
    - **게시글 조회 (전체)**  
        Get /api/v1/posts  
        ex) http://ec2-3-35-225-29.ap-northeast-2.compute.amazonaws.com:8080/api/v1/posts?page=0    
    - **게시글 조회 (단건)**  
        Get /api/v1/posts/{postId}  
        ex) http://ec2-3-35-225-29.ap-northeast-2.compute.amazonaws.com:8080/api/v1/posts/1  
    - **게시글 작성**  
        Post /api/v1/posts  
        ex) http://ec2-3-35-225-29.ap-northeast-2.compute.amazonaws.com:8080/api/v1/posts  
    - **게시글 수정**  
        Put /api/v1/posts/{postId}  
        ex) http://ec2-3-35-225-29.ap-northeast-2.compute.amazonaws.com:8080/api/v1/posts/1  
    - **게시글 삭제**  
        Delete /api/v1/posts/{postId}  
        ex) http://ec2-3-35-225-29.ap-northeast-2.compute.amazonaws.com:8080/api/v1/posts  

## :closed_book: 체크리스트
+ 인증/인가 필터 구현 (JWT token filter)
+ Swagger 사용
+ AWS EC2에 Docker 배포
+ Gitlab CI & Crontab CD 을 통한 AWS 배포 자동화
+ 주요 기능에 대한 Test Code 작성

## :ledger: 1주차 미션 요약
tistory : https://celdan.tistory.com/category/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8/%EB%A9%8B%EC%82%AC%20%EA%B0%9C%EC%9D%B8%20%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%20%28mutsa-SNS%29?page=2  

**[접근 방법]**
+ JWT token filter를 이용하여 권한이 없는 사용자가 접근하였을 때 차단하였으며, 그 때 Response로 exception 메세지를 출력하도록 하였다. (https://celdan.tistory.com/11)  
+ CI/CD 를 통해 main branch에 push 하였을 때 AWS에 자동적으로 배포되도록 하였다. (https://celdan.tistory.com/4)  
+ 각각의 기능에서 성공하는 경우와 exception이 발생하는 경우에 대한 Test Code를 작성하였다. Controller와 Service에 대한 Test Code 모두 작성하였다. (https://celdan.tistory.com/6) (https://celdan.tistory.com/14) (https://celdan.tistory.com/16)  

**[특이사항]**
+ JWT token filter에서 exception 처리 하는 과정에 어려움을 많이 겪었다. 이 exception처리에 대해서는 보다 공부하여 refactoring이 필요 할 듯 하다.
+ CI/CD 과정에서도 많은 시행착오가 발생하였다. repository name에 대문자를 쓰면 안되었고, root directory 안에 새로운 repository를 생성하였을 때, 오류가 발생하였다.
+ deploy file 생성시, image에 대한 제거를 하지 않아 계속하여 쌓였다. image tag가 none인 image들을 image pull할 때 실행하게 하였다. 추후 수정이 필요 할 수 있다.
+ test code 역시 초반에 감을 잡는데 많은 시행착오가 발생하였다. controller와 service의 test의 차이점을 이해하고, 의존성에 대한 고민을 해보는 시간이었다.
+ 리펙토링 시, 불필요한 코드나 로직들을 정리하고 싶으며 변수명이나 method 명에 통일성을 주어야 될 듯 하다.

## :gem: 주요 기능
+ **User**
    - **회원 가입**  
        userName, password 입력  
        UserController.join  
        UserService.join  
        Response  
        ```java
        //성공
        {
            "resultCode": "SUCCESS",
            "result":{
                "userId": 32,
                "userName": "pigeon2gugu"
            }
        }

        //실패 (userName 중복)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "DUPLICATED_USER_NAME",
                "message": "UserName이 중복됩니다."
            }
        }
        ```

    - **로그인**  
        userName, password 입력  
        JWT token 발행  
        UserCotnroller.login  
        UserService.login  
        Response  
        ```java
        //성공 시
        {
            "resultCode": "SUCCESS",
            "result":{
                "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6ImhhbmV1bDIiLCJpYXQiOjE2NzIxMTY2MDIsImV4cCI6MTY3MjEyMDIwMn0.Z3aEqRa7bqjmftNhnyVv36HASg5i2S9hc4XVMMVh3sU"
            }
        }

        //실패 1 (userName 없음)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "NOT_FOUNDED_USER_NAME",
                "message": "UserName is not founded"
            }
        }

        //실패 2
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "INVALID_PASSWORD",
                "message": "패스워드가 잘못되었습니다."
            }
        }
        ```

    - **ADMIN 역할 부여**  
        userRole.ADMIN 유저만 가능  
        UserController.roleChange  
        UserService.changeUserRole  
        Response  
        ```java
        //성공 시
        {
            "resultCode": "SUCCESS",
            "result":{
                "message": "ADMIN 부여 완료",
                "userId": 5
            }
        }

        //실패 1 (토큰 error)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "INVALID_TOKEN",
                "message": "잘못된 토큰입니다."
            }
        }

        //실패 2 (ADMIN이 아닌 유저)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "INVALID_PERMISSION",
                "message": "사용자가 권한이 없습니다."
            }
        }
        ```

+ **Post**
    - **게시글 조회 (전체)**  
        전체 유저 가능  
        한 페이지에 20개의 게시글  
        작성 시간 기준 최신글 순서로 정렬  
        PostController.getPost  
        PostService.getPostAll  
        Response  
        ```java
        //성공 시
        {
            "resultCode": "SUCCESS",
            "result":{
                "content":[{"id": 66, "userName": "kyeongrok22", "title": "hello-title", "body": "hello-body",…],
                "pageable": "INSTANCE",
                "last": true,
                "totalPages": 1,
                "totalElements": 20,
                "size": 20,
                "number": 0,
                "sort":{
                    "empty": true,
                    "sorted": false,
                    "unsorted": true
                },
                "first": true,
                "numberOfElements": 20,
                "empty": false
            }
        }
        
        //실패 1 (sql DB error)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "DATABASE_ERROR",
                "message": "DB에러"
            }
        }
        ```
    - **게시글 조회 (단건)**  
        전체 유저 가능  
        해당 postId의 정보 출력  
        PostController.getPostDetail  
        PostService.detailPost  
        Response  
        ```java
        //성공 시
        {
            "resultCode": "SUCCESS",
            "result":{
                "id": 1,
                "userName": "pigeon2gugu",
                "title": "글 제목",
                "body": "내용",
                "lastModifiedAt": "2022-12-23 09:12:19",
                "createdAt": "2022-12-23 09:12:19"
            }
        }
        
        //실패 1 (sql DB error)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "DATABASE_ERROR",
                "message": "DB에러"
            }
        }

        //실패 2 (해당 post 없음)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "POST_NOT_FOUND",
                "message": "해당 포스트가 없습니다."
            }
        }
        ```

    - **게시글 작성**  
        title, body 입력  
        로그인 유저만 가능  
        PostController.createPost  
        PostService.createPost  
        Response  
        ```java
        //성공 시
        {
            "resultCode": "SUCCESS",
            "result":{
                "message": "포스트 등록 완료",
                "postId": 1
            }
        }

        //실패 1 (토큰 error)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "INVALID_TOKEN",
                "message": "잘못된 토큰입니다."
            }
        }

        //실패 2 (유저 존재 x)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "NOT_FOUNDED_USER_NAME",
                "message": "UserName is not founded"
            }
        }

        //실패 3 (sql DB error)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "DATABASE_ERROR",
                "message": "DB에러"
            }
        }
        ```
    - **게시글 수정**  
        title, body 입력  
        로그인 유저만 가능  
        수정 글 작성 유저 또는 ADMIN 유저만 가능  
        PostController.modifyPost  
        PostService.modifyPost  
        Response  
        ```java
        //성공 시
        {
            "resultCode": "SUCCESS",
            "result":{
                "message": "포스트 수정 완료",
                "postId": 1
            }
        }
        
        //실패 1 (토큰 error)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "INVALID_TOKEN",
                "message": "잘못된 토큰입니다."
            }
        }

        //실패 2 (유저 존재 x)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "NOT_FOUNDED_USER_NAME",
                "message": "UserName is not founded"
            }
        }

        //실패 3 (게시글 존재 x)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "POST_NOT_FOUND",
                "message": "해당 포스트가 없습니다."
            }
        }

        //실패 4 (본인 글 x, ADMIN x)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "INVALID_PERMISSION",
                "message": "사용자가 권한이 없습니다."
            }
        }

        //실패 5 (sql DB error)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "DATABASE_ERROR",
                "message": "DB에러"
            }
        }
        ```

    - **게시글 삭제**  
        로그인 유저만 가능  
        수정 글 작성 유저 또는 ADMIN 유저만 가능  
        PostController.deletePost  
        PostService.deletePost  
        Response  
        ```java
        //성공 시
        {
            "resultCode": "SUCCESS",
            "result":{
                "message": "포스트 수정 완료",
                "postId": 1
            }
        }
        
        //실패 1 (토큰 error)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "INVALID_TOKEN",
                "message": "잘못된 토큰입니다."
            }
        }

        //실패 2 (유저 존재 x)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "NOT_FOUNDED_USER_NAME",
                "message": "UserName is not founded"
            }
        }

        //실패 3 (게시글 존재 x)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "POST_NOT_FOUND",
                "message": "해당 포스트가 없습니다."
            }
        }

        //실패 4 (본인 글 x, ADMIN x)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "INVALID_PERMISSION",
                "message": "사용자가 권한이 없습니다."
            }
        }

        //실패 5 (sql DB error)
        {
            "resultCode": "ERROR",
            "result":{
                "errorCode": "DATABASE_ERROR",
                "message": "DB에러"
            }
        }
        ```








