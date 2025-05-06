/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.service;

import deu.cse.spring_webmail.model.UserAdminAgent;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 *
 * @author jaejin
 */
@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final ServletContext ctx;

    @Value("${root.id}")
    private String rootId;

    @Value("${root.password}")
    private String rootPassword;

    @Value("${admin.id}")
    private String adminId;

    @Value("${james.control.port}")
    private Integer jamesPort;

    @Value("${james.host}")
    private String jamesHost;

    private UserAdminAgent createAgent() {
        String cwd = ctx.getRealPath(".");
        return new UserAdminAgent(jamesHost, jamesPort, cwd, rootId, rootPassword, adminId);
    }

    public boolean addUser(String id, String password) {
        return createAgent().addUser(id, password);
    }

    public void deleteUsers(String[] users) {
        createAgent().deleteUsers(users);
    }

    public List<String> getUserList() {
        List<String> list = createAgent().getUserList();
        list.sort(String::compareTo);
        return list;
    }
}