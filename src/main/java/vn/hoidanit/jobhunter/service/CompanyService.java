package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company createCompany) {
        return this.companyRepository.save(createCompany);
    }

    public List<Company> fetchAllCompanies() {
        return this.companyRepository.findAll();
    }

    public Company handleUpdateCompany(Company requestCompany) {
        Optional<Company> compOptional = this.companyRepository.findById(requestCompany.getId());
        if (compOptional.isPresent()) {

            Company updatedCompany = compOptional.get();
            updatedCompany.setName(requestCompany.getName());
            updatedCompany.setDescription(requestCompany.getDescription());
            updatedCompany.setAddress(requestCompany.getAddress());
            updatedCompany.setLogo(requestCompany.getLogo());

            return this.companyRepository.save(updatedCompany);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }
}
