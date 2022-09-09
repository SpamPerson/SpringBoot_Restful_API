# Study_SpringBoot-Restful-API

1. port = 8081

2. Request URL 
  1) hello-world -> /hello-world , /hello-world-bean , /hello-world-bean/path-variable/{name} , /hello-world-internationalized (All Get)
  2) user -> /users(GET) , /users/{id}(GET), /users (POST) , /users/{id} (DELETE)
  3) jpa/user -> /jpa/users(GET), /jpa/users/{id} (GET), /jpa/users/{id} (DELETE) , /jpa/users(POST)
  4) posts -> /jpa/users/{id}/posts(GET) , /jpa/users/{id}/posts(POST), /jpa/posts/{id}(GET), /jpa/posts/{id}(DELETE)
