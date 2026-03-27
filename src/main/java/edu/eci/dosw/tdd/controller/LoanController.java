package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BorrowBookRequest;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.AuthService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final AuthService authService;

    public LoanController(LoanService loanService, AuthService authService) {
        this.loanService = loanService;
        this.authService = authService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    public Loan borrowBook(@AuthenticationPrincipal CustomUserPrincipal principal,
                           @Valid @RequestBody BorrowBookRequest request) {
        User user = authService.getAuthenticatedUser(principal);
        return loanService.borrowBook(request.bookId(), user.getId());
    }

    @Secured("ROLE_USER")
    @PatchMapping("/{loanId}/return")
    public Loan returnOwnLoan(@AuthenticationPrincipal CustomUserPrincipal principal,
                              @PathVariable String loanId) {
        User user = authService.getAuthenticatedUser(principal);
        return loanService.returnBookForUser(loanId, user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public List<Loan> getOwnLoans(@AuthenticationPrincipal CustomUserPrincipal principal) {
        User user = authService.getAuthenticatedUser(principal);
        return loanService.getLoansByUser(user.getId());
    }

    @Secured("ROLE_LIBRARIAN")
    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @Secured("ROLE_LIBRARIAN")
    @PatchMapping("/{loanId}/return/admin")
    public Loan returnAnyLoan(@PathVariable String loanId) {
        return loanService.returnBook(loanId);
    }
}
