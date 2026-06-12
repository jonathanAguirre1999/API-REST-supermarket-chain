package com.fireboxsys.supermarkets.service;

import com.fireboxsys.supermarkets.dto.SaleDetailsRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleRequestDTO;
import com.fireboxsys.supermarkets.dto.SaleResponseDTO;
import com.fireboxsys.supermarkets.exception.NotEnoughStockException;
import com.fireboxsys.supermarkets.exception.NotFoundException;
import com.fireboxsys.supermarkets.mappers.Mapper;
import com.fireboxsys.supermarkets.model.Product;
import com.fireboxsys.supermarkets.model.Sale;
import com.fireboxsys.supermarkets.model.SaleDetails;
import com.fireboxsys.supermarkets.model.Status;
import com.fireboxsys.supermarkets.repository.BranchRepository;
import com.fireboxsys.supermarkets.repository.ProductRepository;
import com.fireboxsys.supermarkets.repository.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fireboxsys.supermarkets.mappers.Mapper.toDTO;

@Service
public class SaleService implements ISaleService{

    private final SaleRepository saleRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository, BranchRepository branchRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public SaleResponseDTO save(SaleRequestDTO newSaleDTO) {
        Sale s = new Sale();
        List<SaleDetails> saleDetails = new ArrayList<>();
        Double total = 0.0;

        for (SaleDetailsRequestDTO saleDetailsDTO : newSaleDTO.getDetails()) {
            Product p = productRepository
                    .findById(saleDetailsDTO.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: id #" + saleDetailsDTO.getProductId()));

            SaleDetails sd = new SaleDetails();
            sd.setProduct(p);

            if (p.getStock() < saleDetailsDTO.getQuantity()) throw new NotEnoughStockException
                    ("Insufficient stock for product: " + p.getName());

            sd.setQuantity(saleDetailsDTO.getQuantity());

            sd.setPrice(p.getPrice());

            Double subtotal = p.getPrice() * saleDetailsDTO.getQuantity();
            sd.setSubtotal(subtotal);

            sd.setSale(s);

            saleDetails.add(sd);
            total += subtotal;

            //STOCK CONTROL
            p.setStock(p.getStock() - saleDetailsDTO.getQuantity());
        }

        s.setDate(LocalDateTime.now());
        s.setStatus(Status.REGISTERED);
        s.setBranch(branchRepository
                .findById(newSaleDTO.getBranchId())
                .orElseThrow(() -> new NotFoundException("Branch not found: id #" + newSaleDTO.getBranchId())));
        s.setSaleDetails(saleDetails);
        s.setTotal(total);
        return toDTO(saleRepository.save(s));
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDTO findById(Long id) {
         Sale s = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale not found: id #" + id));
         return toDTO(s);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponseDTO> findAll(Pageable pageable) {
        return saleRepository.findAll(pageable).map(Mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponseDTO> findByBranchId(Long branchId, Pageable pageable) {
        return saleRepository.findSalesByBranchId(branchId, pageable).map(Mapper::toDTO);
    }

    @Override
    public SaleResponseDTO update(Long id, SaleRequestDTO saleDTO) {
        throw new UnsupportedOperationException("Sales cannot be modified once registered for audit purposes.");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Sale s = saleRepository.findById(id).orElseThrow(() -> new NotFoundException("Sale not found: id #" + id));

        if (s.getStatus() == Status.CANCELLED) throw new IllegalStateException("Sale #" + id + " already cancelled.");

        s.setStatus(Status.CANCELLED);

        for (SaleDetails d : s.getSaleDetails()) {
            Product p = d.getProduct();
            p.setStock(p.getStock() + d.getQuantity());
        }
    }
}
