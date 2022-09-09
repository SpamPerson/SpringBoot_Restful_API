package com.example.restfulweb.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/jpa")
public class UserJpaController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // http://localhost:8081/jpa/users or http://localhost:8081/users
    @GetMapping("/users")
    public MappingJacksonValue retrieveAllUsers(){
        List<User> users = userRepository.findAll();

        // filter
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(
                "id", "name", "joinDate", "password","ssn","posts"
        );
        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(users);
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping("/users/{id}")
    public MappingJacksonValue retrieveUser(@PathVariable int id){
        Optional<User> user = userRepository.findById(id);

        if(!user.isPresent()) {
            throw new UserNotFoundException(String.format("ID[%s] not found",id));
        }

        // HATEOAS
        EntityModel<User> model = EntityModel.of(user.get());
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkTo.withRel("all-users"));

        // filter
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(
                "id", "name", "joinDate", "password","ssn","posts"
        );
        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo",filter);
        MappingJacksonValue mapping = new MappingJacksonValue(model);
        mapping.setFilters(filters);

        return mapping;
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        userRepository.deleteById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
       User saveUser  = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saveUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    // /jpa/users/10/posts
    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveAllPostsByUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);

        if(!user.isPresent()){
            throw new UserNotFoundException(String.format("ID[%s] not found",id));
        }

        return user.get().getPosts();
    }

    @PostMapping("/users/{id}/posts")
    public ResponseEntity<Post> createPost(@PathVariable int id, @RequestBody Post post) {
        Optional<User> user = userRepository.findById(id);

        if(!user.isPresent()){
            throw new UserNotFoundException(String.format("ID %s not found", id));
        }
        post.setUser(user.get());
        Post savedPost = postRepository.save(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPost.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    // posts 조회
    @GetMapping("/posts/{id}")
    public Optional<Post> retrieveAllPostsByPost(@PathVariable int id) {
        Optional<Post> post = postRepository.findById(id);

        if(!post.isPresent()){
            throw new UserNotFoundException(String.format("ID[%s] not found",id));
        }

        return post;
    }

    // posts 삭제
    @DeleteMapping("/posts/{id}")
    public void deletePost(@PathVariable int id){
        if(!postRepository.findById(id).isPresent()){
           throw new UserNotFoundException(String.format("No posts by %s",id));
        } else {
            postRepository.deleteById(id);
        }
    }

}
