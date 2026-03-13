package exercises.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tenant in the multi-tenant SaaS system.
 *
 * <p>Each tenant owns a set of {@link User Users} and {@link Project Projects}.
 * All child entities carry a {@code tenant_id} foreign key for
 * discriminator-based multi-tenancy.
 *
 * <p><b>Exercise 1 — Multi-Tenant SaaS Data Layer</b></p>
 *
 * <p><b>TODO:</b>
 * <ol>
 *   <li>Add the {@code User} entity with fields: id, email, role, tenant (ManyToOne).</li>
 *   <li>Add the {@code Project} entity with fields: id, name, description, tenant (ManyToOne).</li>
 *   <li>Add the {@code Task} entity with fields: id, title, status, priority, project (ManyToOne).</li>
 *   <li>Consider adding a {@code TenantContext} (ThreadLocal) for automatic filtering.</li>
 *   <li>Implement bidirectional helper methods ({@code addUser}, {@code removeUser}).</li>
 * </ol>
 */
@Entity
@Table(name = "tenants")
@EntityListeners(AuditingEntityListener.class)
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Plan plan = Plan.FREE;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    // TODO: Add @OneToMany for projects
    // @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Project> projects = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // ── Constructors ─────────────────────────────────────────────────

    protected Tenant() {}

    public Tenant(String name, Plan plan) {
        this.name = name;
        this.plan = plan;
    }

    // ── Bidirectional helpers ─────────────────────────────────────────

    /**
     * TODO: Implement helper to add a user and set the inverse side.
     *
     * <pre>{@code
     * public void addUser(User user) {
     *     users.add(user);
     *     user.setTenant(this);
     * }
     * }</pre>
     */

    /**
     * TODO: Implement helper to remove a user and clear the inverse side.
     */

    // ── Getters & Setters ────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public List<User> getUsers() {
        return users;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // ── Inner types ──────────────────────────────────────────────────

    public enum Plan {
        FREE, STARTER, PROFESSIONAL, ENTERPRISE
    }

    // ── Stub entity: User (move to its own file) ─────────────────────

    /**
     * TODO: Move this to a separate {@code User.java} file and flesh out.
     */
    @Entity
    @Table(name = "users")
    @EntityListeners(AuditingEntityListener.class)
    public static class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false, length = 30)
        private String role = "MEMBER";

        @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
        @jakarta.persistence.JoinColumn(name = "tenant_id", nullable = false)
        private Tenant tenant;

        @CreatedDate
        @Column(updatable = false)
        private Instant createdAt;

        @LastModifiedDate
        private Instant updatedAt;

        protected User() {}

        public User(String email, String role) {
            this.email = email;
            this.role = role;
        }

        public Long getId() { return id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Tenant getTenant() { return tenant; }
        public void setTenant(Tenant tenant) { this.tenant = tenant; }
    }
}
