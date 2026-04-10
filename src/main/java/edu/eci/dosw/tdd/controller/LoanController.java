package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.util.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;
    private final SecurityUtils securityUtils;

    public LoanController(LoanService loanService, LoanMapper loanMapper, SecurityUtils securityUtils) {
        this.loanService = loanService;
        this.loanMapper = loanMapper;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/book/{bookId}/user/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN') or @securityUtils.isOwner(#userId)")
    public ResponseEntity<LoanDTO> createLoan(@PathVariable String bookId, @PathVariable String userId) {
        Loan loan = loanService.createLoan(bookId, userId);
        return new ResponseEntity<>(loanMapper.toDTO(loan), HttpStatus.CREATED);
    }

    @PostMapping("/{loanId}/return")
    public ResponseEntity<LoanDTO> returnLoan(@PathVariable String loanId) {
        loanService.validateLoanOwnership(loanId, securityUtils.getCurrentUserId(), securityUtils.isLibrarian());
        Loan loan = loanService.returnLoan(loanId);
        return ResponseEntity.ok(loanMapper.toDTO(loan));
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<LoanDTO> loans = loanService.getAllLoans().stream()
                .map(loanMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN') or @securityUtils.isOwner(#userId)")
    public ResponseEntity<List<LoanDTO>> getLoansByUser(@PathVariable String userId) {
        List<LoanDTO> loans = loanService.getLoansByUserId(userId).stream()
                .map(loanMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<LoanDTO>> getMyLoans() {
        String userId = securityUtils.getCurrentUserId();
        List<LoanDTO> loans = loanService.getLoansByUserId(userId).stream()
                .map(loanMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Void> deleteLoan(@PathVariable String id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}