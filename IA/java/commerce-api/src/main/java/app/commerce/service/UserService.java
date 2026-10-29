package app.commerce.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.commerce.api.ApiDtos.UserInput;
import app.commerce.api.ApiDtos.UserView;
import app.commerce.api.ApiMapper;
import app.commerce.api.ConflictException;
import app.commerce.api.ResourceNotFoundException;
import app.commerce.domain.User;
import app.commerce.repository.OrderRepository;
import app.commerce.repository.UserRepository;
import app.commerce.security.PasswordHasher;

@Service
public class UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+"
        + "@"
        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$"
    );

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PasswordHasher passwordHasher;

    public UserService(
        UserRepository userRepository,
        OrderRepository orderRepository,
        PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional(readOnly = true)
    public List<UserView> findAll() {
        return userRepository
            .findAllByOrderByIdAsc()
            .stream()
            .map(ApiMapper::toUserView)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserView findById(Long id) {
        User user = findEntity(id);

        return ApiMapper.toUserView(user);
    }

    @Transactional
    public UserView create(UserInput input) {
        validateInput(input, true);

        String email = normalizeEmail(input.email);

        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ConflictException(
                "Ja existe um usuario cadastrado com este e-mail."
            );
        }

        User user = new User(
            input.name.trim(),
            email,
            input.phone.trim(),
            passwordHasher.hash(input.password)
        );

        User savedUser = userRepository.saveAndFlush(user);

        return ApiMapper.toUserView(savedUser);
    }

    @Transactional
    public UserView update(Long id, UserInput input) {
        User user = findEntity(id);

        validateInput(input, false);

        String email = normalizeEmail(input.email);

        userRepository.findByEmailIgnoreCase(email)
            .ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ConflictException(
                        "O e-mail informado ja pertence a outro usuario."
                    );
                }
            });

        user.setName(input.name.trim());
        user.setEmail(email);
        user.setPhone(input.phone.trim());

        if (input.password != null) {
            user.setPasswordHash(
                passwordHasher.hash(input.password)
            );
        }

        User updatedUser = userRepository.saveAndFlush(user);

        return ApiMapper.toUserView(updatedUser);
    }

    @Transactional
    public void delete(Long id) {
        User user = findEntity(id);

        if (orderRepository.existsByCustomerId(id)) {
            throw new ConflictException(
                "Este usuario possui pedidos associados e nao pode ser excluido."
            );
        }

        userRepository.delete(user);
        userRepository.flush();
    }

    private User findEntity(Long id) {
        return userRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Usuario nao encontrado: " + id
                )
            );
    }

    private void validateInput(
        UserInput input,
        boolean passwordRequired
    ) {
        if (input == null) {
            throw new IllegalArgumentException(
                "Os dados do usuario sao obrigatorios."
            );
        }

        if (isBlank(input.name) || input.name.trim().length() > 120) {
            throw new IllegalArgumentException(
                "O nome deve conter entre 1 e 120 caracteres."
            );
        }

        if (isBlank(input.email) || input.email.trim().length() > 254) {
            throw new IllegalArgumentException(
                "O e-mail e obrigatorio e deve ter ate 254 caracteres."
            );
        }

        String email = normalizeEmail(input.email);

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(
                "Informe um endereco de e-mail valido."
            );
        }

        if (isBlank(input.phone) || input.phone.trim().length() > 30) {
            throw new IllegalArgumentException(
                "O telefone deve conter entre 1 e 30 caracteres."
            );
        }

        if (passwordRequired || input.password != null) {
            if (
                input.password == null
                || input.password.length() < 8
            ) {
                throw new IllegalArgumentException(
                    "A senha deve conter pelo menos 8 caracteres."
                );
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeEmail(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}