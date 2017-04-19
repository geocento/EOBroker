package com.geocento.webapps.eobroker.common.shared.entities.subscriptions;

import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class Following {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    User user;

    @ManyToOne
    Product product;

    @ManyToOne
    Company company;

    @Temporal(TemporalType.TIMESTAMP)
    Date creationDate;

    public Following() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
