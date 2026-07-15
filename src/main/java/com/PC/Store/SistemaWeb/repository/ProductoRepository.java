package com.PC.Store.SistemaWeb.repository;

import com.PC.Store.SistemaWeb.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository <Producto,Integer> {
    List<Producto> findByStockLessThanEqualOrderByStockAsc(int stock);
}
