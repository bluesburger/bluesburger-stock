package br.com.bluesburger.stock.infra.database.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.bluesburger.stock.domain.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Entity
@Getter
@ToString(exclude = { "product" })
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(schema = "bluesburger-stock", name = "TB_ORDER_STOCK")
@Builder
public class OrderStockEntity implements Serializable {
	
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
    
    @With
    @Setter
    @NotNull
    @Default
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status = Status.PENDING;
    
    @Setter
    @NotNull
    @NonNull
    @Column(name = "ORDER_ID")
    private String orderId;;
    
    @Setter
    @NotNull
    @NonNull
    @ManyToOne
    @JoinColumn(name = "PRODUCT", nullable = false)
    private ProductEntity product;
    
    public OrderStockEntity(Status status, 
    		@NotNull @NonNull UUID orderId, 
    		@NotNull @NonNull ProductEntity product) {
    	this(null, status, orderId, product);
    }
    
    public OrderStockEntity(Long id, 
    		Status status, 
    		@NotNull @NonNull UUID orderId, 
    		@NotNull @NonNull ProductEntity product) {
    	this.id = id;
    	this.status = Optional.ofNullable(status).orElse(Status.PENDING);
    	this.orderId = orderId.toString();
    	this.product = product;
    	this.createdTime = LocalDateTime.now();
    	this.validate();
    }

	public void validate() {
		product.validate();
		if (product.getId() == null || product.getId() <= 0) {
			throw new IllegalArgumentException("É necessário um produto com id válido.");
		}
	}

	public OrderStockEntity reserve() {
    	status = Status.RESERVED;
    	product.reserve();
    	return this;
    }

	public OrderStockEntity schedule() {
		if (!this.status.equals(Status.RESERVED)) {
			throw new IllegalStateException("Stock cannot be scheduled before reserved.", null);
		}
		
		status = Status.SCHEDULED;
		return this;
	}
}
