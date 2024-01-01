package com.prt.bookstore.controllers;

import com.prt.bookstore.Models.ERole;
import com.prt.bookstore.Models.Role;
import com.prt.bookstore.Models.User;
import com.prt.bookstore.Repositories.RoleRepository;
import com.prt.bookstore.Repositories.UserRepository;
import com.prt.bookstore.RequestsResponces.JwtResponse;
import com.prt.bookstore.RequestsResponces.LoginRequest;
import com.prt.bookstore.RequestsResponces.MessageResponse;
import com.prt.bookstore.RequestsResponces.SignupRequest;
import com.prt.bookstore.Services.UserDetailsImpl;
import com.prt.bookstore.configs.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRespository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(), 
						loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		return ResponseEntity.ok(new JwtResponse(jwt,
				userDetails.getId(), 
				userDetails.getUsername(), 
				userDetails.getEmail(), 
				roles));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
		
		if (userRespository.existsByUsername(signupRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username exists"));
		}
		
		if (userRespository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email exists"));
		}
		
		User user = new User(signupRequest.getUsername(),
				signupRequest.getEmail(),
				passwordEncoder.encode(signupRequest.getPassword()));
		
		Set<String> reqRoles = signupRequest.getRoles();
		Set<Role> roles = new HashSet<>();
		
		if (reqRoles == null) {
			Role userRole = roleRepository
					.findByName(ERole.USER)
					.orElseThrow(() -> new RuntimeException("Error, Role USER is not found"));
			roles.add(userRole);
		} else {

			reqRoles.forEach(string -> {
				if(string.equals("admin")){
					Role adminrole = roleRepository.findByName(ERole.ADMIN).orElseThrow(() -> new RuntimeException("Error, Role ADMIN is not found"));
				roles.add(adminrole);
				}else {
					Role userRole = roleRepository.
							findByName(ERole.USER).orElseThrow(() -> new RuntimeException("Error, Role USER is not found"));
					roles.add(userRole);
				}
			});

		}
		user.setRoles(roles);
		userRespository.save(user);
		return ResponseEntity.ok(new MessageResponse("User CREATED"));
	}
}
