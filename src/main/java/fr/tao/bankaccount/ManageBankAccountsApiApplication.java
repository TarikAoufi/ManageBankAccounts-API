package fr.tao.bankaccount;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import fr.tao.bankaccount.dto.account.CurrentAccountDto;
import fr.tao.bankaccount.dto.account.SavingsAccountDto;
import fr.tao.bankaccount.dto.customer.CustomerDto;
import fr.tao.bankaccount.dto.operation.OperationRequestDto;
import fr.tao.bankaccount.enums.OperationType;
import fr.tao.bankaccount.service.AccountService;
import fr.tao.bankaccount.service.CustomerService;
import fr.tao.bankaccount.service.OperationService;
import fr.tao.bankaccount.util.MessageUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024 
 */
@Slf4j
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@OpenAPIDefinition(info = @Info(title = MessageUtil.API_REST_DOC_TITLE, version = "1.0", description = MessageUtil.API_REST_DOC_DESCRIPTION))
public class ManageBankAccountsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManageBankAccountsApiApplication.class, args);
	}

	@Bean
	CommandLineRunner start(CustomerService customerService,
			AccountService accountService,
			OperationService operationService) {
		return args -> {
			log.info("########### Create customers #############");
			Stream.of("Karim", "Jihane", "Ali").forEach(name -> {
				try {
					var customerDto = new CustomerDto();
					customerDto.setName(name);
					customerDto.setEmail(name.toLowerCase() + "@gmail.com");				
					customerService.saveCustomer(customerDto);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			
			log.info("########### Create accounts #############");
			customerService.getAllCustomer().forEach(c -> {
				try {
					// Current account
					var currentAccountDto = new CurrentAccountDto();
					currentAccountDto.setBalance(BigDecimal.valueOf(Math.random() * 9000));
					currentAccountDto.setOverdraftLimit(new BigDecimal(2000));					
					accountService.createAccount(c.getId(), currentAccountDto, CurrentAccountDto.class);
					
					// Savings account
					var savingsAccountDto = new SavingsAccountDto();
					savingsAccountDto.setBalance(BigDecimal.valueOf(Math.random() * 9000));
					savingsAccountDto.setInterestRate(BigDecimal.valueOf(7.4));		
					accountService.createAccount(c.getId(), savingsAccountDto, SavingsAccountDto.class);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			
			log.info("########### Create operations #############");
			accountService.getAllAccount().forEach(account -> {
				for (int i = 0; i < 5; i++) {
					try {
						if (account instanceof CurrentAccountDto) {
							var operationRequestDto1 = OperationRequestDto.builder()
									.accountId(account.getId())
									.amount(BigDecimal.valueOf(1000 + Math.random() * 1100))
									.operationType(OperationType.DEPOSIT)
									.build();
							operationService.performOperation(operationRequestDto1);
							
							var operationRequestDto2 = OperationRequestDto.builder()
									.accountId(account.getId())
									.amount(BigDecimal.valueOf(1000 + Math.random() * 1100))
									.operationType(OperationType.WITHDRAWAL)
									.build();
							operationService.performOperation(operationRequestDto2);
						} else if (account instanceof SavingsAccountDto) {
							var operationRequestDto = OperationRequestDto.builder()
									.accountId(account.getId())
									.amount(BigDecimal.valueOf(1000 + Math.random() * 1100))
									.operationType(OperationType.DEPOSIT)
									.build();
							operationService.performOperation(operationRequestDto);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

			});
			
		};
	}

}
