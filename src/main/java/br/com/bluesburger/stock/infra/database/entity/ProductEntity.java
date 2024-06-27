package br.com.bluesburger.stock.infra.database.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.bluesburger.stock.domain.exception.OutOfStockException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
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
    @Column(name = "QUANTITY")
    private Integer quantity;
    
    @NotNull
    @NonNull
    @Column(name = "ORDERS")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    private List<OrderStockEntity> orders;
    
    public ProductEntity reserve() {
    	if (quantity == 0) {
    		throw new OutOfStockException();
    	}
    	
    	quantity--;
    	return this;
    }
}
