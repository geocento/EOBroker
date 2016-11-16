package com.geocento.webapps.eobroker.common.shared.entities.orders;

import com.geocento.webapps.eobroker.common.server.Utils.GeometryConverter;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ImageProductEntity {

	@Id
	@GeneratedValue
	Long id;
	
    String productId;

    @Enumerated(EnumType.STRING)
    Product.TYPE type;

    @Convert(converter = GeometryConverter.class)
    String coordinatesWKT;

    String satelliteName;

    String instrumentName;

    String modeName;

    @Temporal(TemporalType.TIMESTAMP)
    Date start;

    @Temporal(TemporalType.TIMESTAMP)
    Date stop;

    Integer orbit;

    Double centerLat;

    Double centerLon;

    String orbitDirection;

    String ascendingNodeDate;

    String startTimeFromAscendingNode;

    String completionTimeFromAscendingNodeDate;

    String productType;
	
    // statistics
	double observationCoverage;

    double coveragePercent;

    double usefulAreaPercent;
    
	// json string according to sensor vendorAttributes
    @Column(length = 1000)
    String options;

    // frame properties
    int frameNumbers = 0;
	
	/*
	 * this is the fetch Url if the image is directly available
	 */
	String fetchUrl;

    public ImageProductEntity() {
	}

	public String getProductId() {
		return productId;
	}
    
    public void setProductId(String productId) {
		this.productId = productId;
	}

    public Product.TYPE getType() {
        return type;
    }

    public void setType(Product.TYPE type) {
        this.type = type;
    }

    public String getCoordinatesWKT() {
        return coordinatesWKT;
    }

    public void setCoordinatesWKT(String coordinatesWKT) {
        this.coordinatesWKT = coordinatesWKT;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getStop() {
		return stop;
	}

	public void setStop(Date stop) {
		this.stop = stop;
	}

	public Integer getOrbit() {
		return orbit;
	}

	public void setOrbit(Integer orbit) {
		this.orbit = orbit;
	}

	public Double getCenterLat() {
		return centerLat;
	}

	public void setCenterLat(Double centerLat) {
		this.centerLat = centerLat;
	}

	public Double getCenterLon() {
		return centerLon;
	}

	public void setCenterLon(Double centerLon) {
		this.centerLon = centerLon;
	}

	public String getOrbitDirection() {
		return orbitDirection;
	}

	public void setOrbitDirection(String orbitDirection) {
		this.orbitDirection = orbitDirection;
	}

	public String getAscendingNodeDate() {
		return ascendingNodeDate;
	}

	public void setAscendingNodeDate(String ascendingNodeDate) {
		this.ascendingNodeDate = ascendingNodeDate;
	}

	public String getStartTimeFromAscendingNode() {
		return startTimeFromAscendingNode;
	}

	public void setStartTimeFromAscendingNode(String startTimeFromAscendingNode) {
		this.startTimeFromAscendingNode = startTimeFromAscendingNode;
	}

	public String getCompletionTimeFromAscendingNodeDate() {
		return completionTimeFromAscendingNodeDate;
	}

	public void setCompletionTimeFromAscendingNodeDate(
			String completionTimeFromAscendingNodeDate) {
		this.completionTimeFromAscendingNodeDate = completionTimeFromAscendingNodeDate;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	// TODO - find another way of storing the information
	public void setObservationCoverage(double observationCoverage) {
		this.observationCoverage = observationCoverage;
	}
	
	public double getObservationCoverage() {
		return observationCoverage;
	}
	
	// set the coverage percentage as a double value between 0.0 to 1.0
	public void setCoveragePercent(double coveragePercent) {
		this.coveragePercent = coveragePercent;
	}
	
	// get the coverage percentage as a double value between 0.0 to 1.0
	public double getCoveragePercent() {
		return coveragePercent;
	}

	public void setUsefulAreaPercent(double usefulArea) {
		this.usefulAreaPercent = usefulArea;
	}

	public double getUsefulAreaPercent() {
		return usefulAreaPercent;
	}
	
	public int getFrameNumbers() {
		return frameNumbers;
	}
	
	public void setFrameNumbers(int frameNumbers) {
		this.frameNumbers = frameNumbers;
	}
    
	public String getFetchUrl() {
		return fetchUrl;
	}
	
	public void setFetchUrl(String fetchUrl) {
		this.fetchUrl = fetchUrl;
	}

    public void setSatelliteName(String satelliteName) {
        this.satelliteName = satelliteName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public String getSatelliteName() {
		return satelliteName;
	}

	public String getSensorName() {
		return instrumentName;
	}

	public String getModeName() {
		return modeName;
	}

}
