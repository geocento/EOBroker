package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.requests.ImagesServiceResponseDTO;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ImagesResponseView extends RequestView {

    void setPresenter(Presenter presenter);

    void displayImagesRequest(ImagesServiceResponseDTO imagesServiceResponseDTO);

    public interface Presenter extends RequestView.Presenter {
    }

}
