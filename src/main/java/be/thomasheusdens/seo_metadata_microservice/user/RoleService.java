package be.thomasheusdens.seo_metadata_microservice.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepo;

    public RoleService(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    public Optional<Role> findByName(String roleName) {
        return roleRepo.findByName(roleName);
    }

    public List<Role> findAll() {
        return roleRepo.findAll();
    }

    public Role createRole(Role role) {
        return roleRepo.save(role);
    }
}