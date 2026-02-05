package tn.enis.pfa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
	private List<Course> coursesCreated;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
	private List<Enrollment> enrollments;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
	private List<Progress> progress;

	// ✅ GETTERS
	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Role getRole() {
		return role;
	}

	// ✅ SETTERS (CE QUI MANQUAIT)
	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
