package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface SearchPageView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void setPresenter(Presenter presenter);

    void setSearchText(String search, boolean forceFocus);

    void displayAoI(AoIDTO aoi);

    void displayLoadingResults(String message);

    void hideLoadingResults();

    void clearResults();

    void setTitleText(String title);

    void setMatchingProducts(List<ProductDTO> suggestedProducts, String moreUrl);

    void setMatchingServices(List<ProductServiceDTO> productServices, String moreUrl);

    void addProducts(List<ProductDTO> products, int start, boolean hasMore, String text);

    void setMatchingDatasets(List<ProductDatasetDTO> productDatasets, String moreUrl);

    void setMatchingSoftwares(List<SoftwareDTO> softwares, String moreUrl);

    void setMatchingProjects(List<ProjectDTO> projects, String moreUrl);

    void setMatchingImagery(String text);

    TemplateView getTemplateView();

    void setDatasetProviders(List<DatasetProviderDTO> datasetProviderDTOs, final String text, AoIDTO aoi);

    void setResultsTitle(String message);

    void displayFilters(Category category);

    void addProductServices(List<ProductServiceDTO> products, int start, boolean hasMore, String text);

    void addProductDatasets(List<ProductDatasetDTO> products, int start, boolean hasMore, String text);

    void addSoftware(List<SoftwareDTO> softwareDTOs, int start, boolean hasMore, String text);

    void addProjects(List<ProjectDTO> projectDTOs, int start, boolean hasMore, String text);

    void addCompanies(List<CompanyDTO> companyDTOs, int start, boolean hasMore, String text);

    HasValue<Boolean> getFilterByAoI();

    ServiceType getProductServiceType();

    HasValue<Boolean> getTimeFrameFilterActivated();

    HasValue<Date> getStartTimeFrameFilter();

    HasValue<Date> getStopTimeFrameFilter();

    Sector getSectorFilter();

    Thematic getThematicFilter();

    void centerOnAoI();

    COMPANY_SIZE getCompanySizeFilter();

    int getCompanyAgeFilter();

    String getCompanyCountryFilter();

    SoftwareType getSoftwareType();

    void showFilters(boolean display);

    public interface Presenter {

        void loadMoreProducts();

        void loadMoreProductServices();

        void loadMoreProductDatasets();

        void loadMoreSofware();

        void loadMoreProjects();

        void aoiChanged(AoIDTO aoi);

        void filtersChanged();

        void aoiSelected(AoIDTO aoi);
    }

}
