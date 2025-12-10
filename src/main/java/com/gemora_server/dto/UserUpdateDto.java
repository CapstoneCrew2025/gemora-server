package com.gemora_server.dto;

import lombok.*;

@Getter
//lombok annotations to generate getters, setters, constructors, and builder pattern methods
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
    private String name;
    private String email;
    private String contactNumber;
    private String role;
}





// 001 Spring Boot Annotations — Quick Guide (copy into your code as notes)
// 002 @SpringBootApplication = ( @Configuration + @EnableAutoConfiguration + @ComponentScan ) starter.
// 003 Usage: put on main class to bootstrap the app.
// 004 @Configuration = tells Spring “this class defines beans (configuration)”.
/* 005 Usage: create @Bean methods inside to register objects in Spring container. */
// 006 @Bean = creates a Spring-managed object from a method return value.
// 007 Usage: @Bean public PasswordEncoder encoder(){ return new BCryptPasswordEncoder(); }
// 008 @Component = generic bean for simple helper classes.
// 009 Usage: Spring will auto-detect and create it when component scanning runs.
// 010 @Service = bean for business logic layer (services).
// 011 Usage: use it on classes that handle rules/logic, call repositories, etc.
// 012 @Repository = bean for data layer; also converts DB exceptions to Spring exceptions.
// 013 Usage: on DAO/Repository classes; with Spring Data, interfaces extend JpaRepository.
// 014 @Controller = MVC controller (returns views, templates).
// 015 @RestController = @Controller + @ResponseBody (returns JSON directly).
// 016 Usage: typical for REST APIs: @RestController public class UserController { ... }
// 017 @RequestMapping = maps URL path for a controller/class or method.
// 018 Usage: @RequestMapping("/api/users") on class; method paths append.
// 019 @GetMapping = shortcut for @RequestMapping(method=GET).
// 020 Usage: @GetMapping("/{id}") public UserDto get(@PathVariable String id) { ... }
// 021 @PostMapping = shortcut for POST routes.
// 022 @PutMapping = shortcut for PUT routes.
// 023 @DeleteMapping = shortcut for DELETE routes.
// 024 @PatchMapping = shortcut for PATCH routes (partial update).
// 025 @PathVariable = reads a path segment from URL.
// 026 Usage: /users/{id} -> method(@PathVariable Long id)
// 027 @RequestParam = reads query param (?page=1&size=10).
// 028 Usage: method(@RequestParam(defaultValue="1") int page)
// 029 @RequestBody = reads JSON body into Java object.
// 030 Usage: method(@RequestBody CreateUserRequest req)
// 031 @ResponseStatus = set HTTP status code for success or error.
// 032 Usage: @ResponseStatus(HttpStatus.CREATED) on POST method.
// 033 @CrossOrigin = allow CORS for frontend domains.
// 034 Usage: @CrossOrigin(origins="http://localhost:5173")
// 035 @Valid = triggers bean validation on request body / params.
// 036 Usage: method(@Valid @RequestBody UserRequest req)
// 037 Validation annotations (javax/jakarta): @NotNull, @NotBlank, @Size, @Email, etc.
// 038 @Validated = enables validation at class level (especially for method params).
// 039 @ExceptionHandler = handle exceptions inside a controller.
// 040 Usage: @ExceptionHandler(NotFoundException.class) ResponseEntity<?> handle(...)
// 041 @ControllerAdvice / @RestControllerAdvice = global exception handling for all controllers.
// 042 Usage: central place for error JSON format.
// 043 @ResponseBody = return the method value as HTTP response body.
// 044 Note: @RestController already includes it, so you don’t need it there.
// 045 @Autowired = inject dependencies (constructor injection is recommended).
// 046 Usage: prefer constructor injection: private final Service s; public C(Service s){this.s=s;}
// 047 @Qualifier = choose one bean when multiple beans of same type exist.
// 048 Usage: @Qualifier("smsSender") NotificationSender sender
// 049 @Primary = marks a bean as default choice when multiple exist.
// 050 Usage: put on one implementation to avoid ambiguity.
// 051 @Value = inject values from properties/env.
// 052 Usage: @Value("${jwt.secret}") private String secret;
// 053 @ConfigurationProperties = bind a group of properties to a class.
// 054 Usage: @ConfigurationProperties(prefix="file") class FileProps { String uploadDir; }
// 055 @EnableConfigurationProperties = enables @ConfigurationProperties classes.
// 056 Usage: in a config class or main class if needed.
// 057 @Profile = activate bean only for specific environment.
// 058 Usage: @Profile("dev") on a bean for development only.
// 059 @ConditionalOnProperty = create bean only if a property exists/has value.
// 060 Usage: helpful for feature flags.
// 061 @Transactional = manage DB transactions (commit/rollback).
// 062 Usage: put on service methods that change DB; rollback on RuntimeException by default.
// 063 @EnableTransactionManagement = enables transactional support (often auto-enabled).
// 064 @Entity = JPA entity mapped to a DB table.
// 065 Usage: annotate model class; must have @Id field.
// 066 @Table = customize table name for @Entity.
// 067 Usage: @Table(name="users")
// 068 @Id = primary key field.
// 069 @GeneratedValue = auto-generate IDs.
// 070 Usage: @GeneratedValue(strategy=GenerationType.IDENTITY)
// 071 @Column = customize column name, constraints.
// 072 Usage: @Column(nullable=false, unique=true)
// 073 @OneToMany, @ManyToOne, @OneToOne, @ManyToMany = JPA relationships.
// 074 Usage: map relations between entities; be careful with JSON recursion.
// 075 @JoinColumn = specify FK column for relationship.
// 076 Usage: @ManyToOne @JoinColumn(name="seller_id")
// 077 @Enumerated = store enums as STRING or ORDINAL.
// 078 Usage: @Enumerated(EnumType.STRING) private Role role;
// 079 @JsonIgnore / @JsonManagedReference / @JsonBackReference = Jackson JSON control.
// 080 Usage: avoid infinite loops when entities reference each other.
// 081 @Data (Lombok) = generates getters/setters/toString/equals/hashCode.
// 082 Warning: with JPA entities, be careful using @Data due to equals/hashCode + lazy fields.
// 083 @Getter / @Setter (Lombok) = generate only getters/setters (safer than @Data sometimes).
// 084 @NoArgsConstructor / @AllArgsConstructor (Lombok) = constructors.
// 085 @Builder (Lombok) = builder pattern for object creation.
// 086 Usage: UserDto.builder().id(id).name(name).build();
// 087 @Slf4j (Lombok) = auto creates logger: log.info("...").
// 088 @EnableWebSecurity = enable Spring Security configuration (for secured APIs).
// 089 @PreAuthorize = method-level authorization checks.
// 090 Usage: @PreAuthorize("hasRole('ADMIN')") on service/controller methods.
// 091 @EnableMethodSecurity = enables @PreAuthorize / @PostAuthorize.
// 092 @RequestHeader = read headers (like Authorization).
// 093 Usage: method(@RequestHeader("Authorization") String token)
// 094 @Async = run method asynchronously in separate thread.
// 095 Usage: needs @EnableAsync; good for emails/notifications.
// 096 @Scheduled = run tasks on schedule.
// 097 Usage: needs @EnableScheduling; e.g., cleanup job daily.
// 098 @SpringBootTest = integration test bootstrapping Spring context.
// 099 @WebMvcTest = MVC slice test for controllers.
// 100 Tip: annotations help Spring “discover + configure + wire” classes automatically.
