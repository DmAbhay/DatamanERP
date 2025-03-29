package dataman.erp.auth.service;

import dataman.erp.auth.dto.UserMast;
import dataman.erp.auth.repository.UserMastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserMastService implements UserDetailsService {

    @Autowired
    private UserMastRepository userMastRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;  // Inject PasswordEncoder


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("Username sent correctly in service layer");
        UserMast user = userMastRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();

    }

    public boolean isUserExist(String username){
        return userMastRepository.existsByUsername(username);
    }
}
