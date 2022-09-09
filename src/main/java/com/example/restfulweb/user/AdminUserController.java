package com.example.restfulweb.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin") // -> 전체 Request URL 앞에 "/admin" 추가
public class AdminUserController {
    private UserDaoService service;

    public AdminUserController(UserDaoService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public MappingJacksonValue retrieveAllUsers() {
        List<User> users = service.findAll();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joindDate", "password");

        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(users);
        mapping.setFilters(filters);

        return mapping;
    }

    // GET /admin/users/1 -> /admin/v1/users/1
    // @GetMapping("/v1/users/{id}") //Request URL Version 관리
    // @GetMapping(value = "/users/{id}/", params = "version=1") Request parameter Version 관리 ex) /admin/users/{id}/?version=1
    // @GetMapping(value = "/users/{id}", headers = "X-API-VERSION=1") Header Version 관리 Header -> Key: X-API-VERSION , Value: 1
    @GetMapping(value = "/users/{id}", produces = "application/vnd.company.appv1+json")
    //media type Version 관리 Header -> key: Accept , Value: application/vnd.company.appv1+json (company.appv1 변경 가능)
    public MappingJacksonValue retrieveUserV1(@PathVariable int id) {
        User user = service.findOne(id);

        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "password", "ssn");

        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(user);
        mapping.setFilters(filters);

        return mapping;
    }

    // GET /admin/users/1 -> /admin/v2/users/1
    // @GetMapping("/v2/users/{id}") //-> Request URL Version 관리
    // @GetMapping(value = "/users/{id}/", params = "version=2") //Request parameter Version 관리 ex) /admin/users/{id}/?version=2
    // @GetMapping(value = "/users/{id}", headers = "X-API-VERSION=2") //Header Version 관리 Header -> Key: X-API-VERSION , Value: 2
    @GetMapping(value = "/users/{id}", produces = "application/vnd.company.appv2+json")
    //media type Version 관리 Header -> key: Accept , Value: application/vnd.company.appv2+json (company.appv2 변경 가능)
    public MappingJacksonValue retrieveUserV2(@PathVariable int id) {
        User user = service.findOne(id);

        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }

        //User -> UserV2
        UserV2 userV2 = new UserV2();
        BeanUtils.copyProperties(user, userV2);
        userV2.setGrade("VIP");

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "grade");

        FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfoV2", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(userV2);
        mapping.setFilters(filters);

        return mapping;
    }
}
