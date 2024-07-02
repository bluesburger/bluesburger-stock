package br.com.bluesburger.stock.infra.database.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.bluesburger.stock.application.Ean;
import br.com.bluesburger.stock.domain.exception.OutOfStockException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "bluesburger-stock", name = "TB_PRODUCT")
@Builder
public class ProductEntity implements Serializable {
	
	private static final long serialVersionUID = 4781858089323528412L;

	@Id
	@Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
    private Long id;

    @CreationTimestamp
    @Column(name = "CREATED_TIME")
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "UPDATED_TIME")
    private LocalDateTime updatedTime;
    
    @NotNull
    @NonNull
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "EAN")
    private String ean;
    
    @NotNull
    @NonNull
    @Column(name = "QUANTITY")
    private Integer quantity;
    
    @OneToMany(mappedBy = "product")
    private List<OrderStockEntity> orders;
    
    public ProductEntity(Long id, @NotNull @NonNull String name, @NotNull @NonNull Integer quantity) {
    	this.id = id;
    	this.name = name;
    	this.quantity = quantity;
    	this.validate();
    }
    
    public void validate() {
    	ObjectUtils.requireNonEmpty(this.name, "Produto com nome inválido.");
    	ObjectUtils.requireNonEmpty(this.quantity, "Produto com quantidade inválida.");
    	
    	if (this.quantity < 0) {
    		throw new IllegalArgumentException("Product with invalid quantity");
    	}
    }
    
    @PostPersist
    public void defineEan() throws Exception {
    	if (this.id == null || this.id <= 0) {
    		throw new IllegalArgumentException("Id is needed to define EAN");
    	}
    	
    	var ean = Ean.defineEan(this.id);
    	this.ean = ean;
    }
    
    public ProductEntity reserve() {
    	if (quantity <= 0) {
    		throw new OutOfStockException();
    	}
    	
    	quantity--;
    	return this;
    }
}
