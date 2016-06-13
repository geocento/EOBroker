package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.customer.client.services.AssetsService;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.*;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import org.apache.log4j.Logger;

import javax.ws.rs.Path;

@Path("/")
public class AssetsResource implements AssetsService {

    Logger logger = Logger.getLogger(AssetsResource.class);

    public AssetsResource() {
        logger.info("Starting service...");
    }

    @Override
    public AoIDTO getAoI(Long id) {
        return new AoIPolygonDTO();
    }

    @Override
    public Long addAoI(AoIDTO aoi) {
        return null;
    }

    @Override
    public void updateAoI(AoIDTO aoi) {

    }

    @Override
    public ProductDTO getProduct(Long id) {
        return null;
    }

    @Override
    public Long addProduct(ProductDTO productDTO) {
        try {
            Product product = ProductHelper.addProduct(productDTO.getName(), productDTO.getDescription(), productDTO.getSector(), productDTO.getThematic());
            return product.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void updateProduct(ProductDTO product) {

    }

    @Override
    public ProductServiceDTO getProductService(Long id) {
        return null;
    }

    @Override
    public Long addProductService(ProductServiceDTO productService) {
        return null;
    }

    @Override
    public void updateProductService(ProductServiceDTO product) {

    }

    @Override
    public CompanyDTO getCompany(Long id) {
        return null;
    }

    @Override
    public Long addCompany(CompanyDTO company) {
        return null;
    }

    @Override
    public void updateCompany(CompanyDTO product) {

    }
}
