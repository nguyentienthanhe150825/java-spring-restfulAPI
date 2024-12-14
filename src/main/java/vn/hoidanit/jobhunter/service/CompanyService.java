package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.Meta;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
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

    // public List<Company> fetchAllCompanies(Pageable pageable) {
    //     Page<Company> pageCompany = this.companyRepository.findAll(pageable);
    //     // convert Page into List
    //     List<Company> listCompanies = pageCompany.getContent();
    //     return listCompanies;
    // }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> spec, Pageable pag) {
        Page<Company> pCompany = this.companyRepository.findAll(spec, pag);
        ResultPaginationDTO result = new ResultPaginationDTO();
       Meta meta = new Meta();

       meta.setPage(pag.getPageNumber() + 1);
        meta.setPageSize(pag.getPageSize());

        meta.setPages(pCompany.getTotalPages());
        meta.setTotal(pCompany.getTotalElements());

        result.setMeta(meta);
        result.setResult(pCompany.getContent());

        return result;
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

    public Company fetchCompanyById (long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            return companyOptional.get();
        }
        return null;
    }
}
