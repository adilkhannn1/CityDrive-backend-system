package kz.citydrive.admin.security;

import kz.citydrive.admin.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AdminUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) {
        return userRepository.findByPhone(phone)
                .map(AdminUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + phone));
    }
}
