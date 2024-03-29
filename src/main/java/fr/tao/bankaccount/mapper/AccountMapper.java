package fr.tao.bankaccount.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import fr.tao.bankaccount.dto.account.AccountDetailsDto;
import fr.tao.bankaccount.dto.account.AccountDto;
import fr.tao.bankaccount.dto.account.CurrentAccountAllDto;
import fr.tao.bankaccount.dto.account.CurrentAccountDto;
import fr.tao.bankaccount.dto.account.SavingsAccountAllDto;
import fr.tao.bankaccount.dto.account.SavingsAccountDto;
import fr.tao.bankaccount.entity.Account;
import fr.tao.bankaccount.entity.CurrentAccount;
import fr.tao.bankaccount.entity.SavingsAccount;
import fr.tao.bankaccount.util.MessageUtil;

/**
 * Mapper interface for converting between different representations of
 * accounts.
 * 
 * @author T. Aoufi
 * @version 1.0
 * @since 17/03/2024
 */
@Mapper(componentModel = "spring", uses = {CustomerMapper.class, OperationMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
	
	/**
     * Instance of the AccountMapper interface generated by MapStruct.
     */
	AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
	
	/**
     * Updates an existing Account entity based on the data in the provided AccountDto.
     *
     * @param accountDto The data to update the Account entity.
     * @param account    The target Account entity to be updated.
     */
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateAccountFromDto(AccountDto accountDto, @MappingTarget Account account);
	
	/**
     * Converts an Account entity to its corresponding DTO representation.
     *
     * @param account The Account entity to be converted.
     * @return The corresponding AccountDto.
     */
    @Mapping(source = "customer", target = "customerDto")
	AccountDto toDto(Account account);
    
    /**
     * Converts an AccountDto to its corresponding entity representation.
     *
     * @param accountDto The AccountDto to be converted.
     * @return The corresponding Account entity.
     */
	@Mapping(source = "customerDto", target = "customer")
	Account toEntity(AccountDto accountDto);
	
	/**
     * Maps an Account entity to its corresponding AccountDto based on its account type.
     *
     * @param account The Account entity to be mapped.
     * @return The corresponding AccountDto.
     * @throws IllegalArgumentException if the account type is not recognized.
     */
	default AccountDto mapAccount(Account account) {
		switch (account.getAccountType()) {
		case CURRENT: 
			return toDto((CurrentAccount) account);
		case SAVINGS:
			return toDto((SavingsAccount) account);
		default:
			throw new IllegalArgumentException(MessageUtil.ACCOUNT_TYPE_NOT_FOUND);
		}
	}
	
	/**
     * Maps an AccountDto to its corresponding Account entity based on its account type.
     *
     * @param accountDto The AccountDto to be mapped.
     * @return The corresponding Account entity.
     * @throws IllegalArgumentException if the account type is not recognized.
     */
	default Account mapAccountDto(AccountDto accountDto) {	
        if (accountDto instanceof CurrentAccountDto currentAccountDto) {
        	return toEntity(currentAccountDto);
        } else if (accountDto instanceof SavingsAccountDto savingsAccountDto) {
            return toEntity(savingsAccountDto);
        } else {
        	throw new IllegalArgumentException(MessageUtil.ACCOUNT_TYPE_NOT_FOUND);
	    }  
    }
	
	/**
     * Maps an Account entity to its corresponding AccountDetailsDto based on its account type.
     *
     * @param account The Account entity to be mapped.
     * @return The corresponding AccountDetailsDto.
     * @throws IllegalArgumentException if the account type is not recognized.
     */
	default AccountDetailsDto mapAccountDetails(Account account) {
		switch (account.getAccountType()) {
		case CURRENT: 
			return toCurrentAccountAllDto((CurrentAccount) account);
		case SAVINGS:
			return toSavingsAccountAllDto((SavingsAccount) account);
		default:
			throw new IllegalArgumentException(MessageUtil.ACCOUNT_TYPE_NOT_FOUND);
		}
	}
	
	/* ============== Methods for CurrentAccount */
	
	/**
     * Converts a CurrentAccount entity to its corresponding CurrentAccountDto.
     *
     * @param currentAccount The CurrentAccount entity to be converted.
     * @return The corresponding CurrentAccountDto.
     */
	@Mapping(source = "customer", target = "customerDto")
	CurrentAccountDto toDto(CurrentAccount currentAccount);	
	
	/**
     * Converts a CurrentAccountDto to its corresponding CurrentAccount entity.
     *
     * @param currentAccountDto The CurrentAccountDto to be converted.
     * @return The corresponding CurrentAccount entity.
     */
	@Mapping(source = "customerDto", target = "customer")
    CurrentAccount toEntity(CurrentAccountDto currentAccountDto);
	
	/**
     * Updates an existing CurrentAccount entity based on the data in the provided CurrentAccountDto.
     *
     * @param currentAccountDto The data to update the CurrentAccount entity.
     * @param currentAccount    The target CurrentAccount entity to be updated.
     */
	@Mapping(source = "customerDto", target = "customer")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateCurrentAccountFromDto(CurrentAccountDto currentAccountDto, @MappingTarget CurrentAccount currentAccount);
	
	/**
     * Converts a CurrentAccount entity to its corresponding CurrentAccountAllDto.
     *
     * @param currentAccount The CurrentAccount entity to be converted.
     * @return The corresponding CurrentAccountAllDto.
     */
	@Mapping(target = "accountDto", source = "currentAccount")
    @Mapping(source = "operations", target = "operationDtos")
    CurrentAccountAllDto toCurrentAccountAllDto(CurrentAccount currentAccount);
    
	
    /* ============== Methods for SavaingAccount */
	
	/**
     * Converts a SavingsAccount entity to its corresponding SavingsAccountDto.
     *
     * @param savingsAccount The SavingsAccount entity to be converted.
     * @return The corresponding SavingsAccountDto.
     */
    @Mapping(source = "customer", target = "customerDto")
	SavingsAccountDto toDto(SavingsAccount savingsAccount);
    
    /**
     * Converts a SavingsAccountDto to its corresponding SavingsAccount entity.
     *
     * @param savingsAccountDto The SavingsAccountDto to be converted.
     * @return The corresponding SavingsAccount entity.
     */
    @Mapping(source = "customerDto", target = "customer")
    SavingsAccount toEntity(SavingsAccountDto savingsAccountDto);
	
    /**
     * Updates an existing SavingsAccount entity based on the data in the provided SavingsAccountDto.
     *
     * @param savingsAccountDto The data to update the SavingsAccount entity.
     * @param savingsAccount    The target SavingsAccount entity to be updated.
     */
	@Mapping(source = "customerDto", target = "customer")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateSavingsAccountFromDto(SavingsAccountDto savingsAccountDto, @MappingTarget SavingsAccount savingsAccount);
	
	/**
     * Converts a SavingsAccount entity to its corresponding SavingsAccountAllDto.
     *
     * @param savingsAccount The SavingsAccount entity to be converted.
     * @return The corresponding SavingsAccountAllDto.
     */
	@Mapping(target = "accountDto", source = "savingsAccount")
	@Mapping(source = "operations", target = "operationDtos")
    SavingsAccountAllDto toSavingsAccountAllDto(SavingsAccount savingsAccount);
	
}
