package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
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

    public ResultPaginationDTO fetchAllCompanies(Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
       Meta meta = new Meta();

       meta.setPage(pageCompany.getNumber());
        meta.setPageSize(pageCompany.getSize());

        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageCompany.getContent());

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
}
