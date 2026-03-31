import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface Categoria {
  id: number;
  nombre: string;
}

interface Producto {
  id?: number;
  categoriaId: number | null;
  codigoInterno: string;
  nombre: string;
  precioVenta: number | null;
  precioCompra: number | null;
  moneda?: string;
}

@Component({
  selector: 'app-producto',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './producto.component.html',
  styleUrl: './producto.component.css',
})
export class ProductoComponent {
  productos: Producto[] = [];
  categorias: Categoria[] = [];
  mostrarFormulario = false;
  editProducto: Producto | null = null;

  // Autocomplete:
  busquedaCategoriaInput: string = '';
  categoriasFiltradas: Categoria[] = [];

  // Buscadores de la tabla:
  busquedaCategoria: string = '';
  busquedaProducto: string = '';

  formProducto: Producto = {
    categoriaId: null,
    codigoInterno: '',
    nombre: '',
    precioVenta: null,
    precioCompra: null,
    moneda: 'Soles',
  };

  private apiUrl = 'http://localhost:8080/api/productos';
  private categoriaUrl = 'http://localhost:8080/api/categorias';

  constructor(private http: HttpClient) {
    this.cargarProductos();
    this.cargarCategorias();
  }

  cargarCategorias() {
    this.http.get<Categoria[]>(this.categoriaUrl).subscribe(data => {
      this.categorias = data;
      this.categoriasFiltradas = data; // inicializa filtradas
    });
  }

  cargarProductos() {
    this.http.get<Producto[]>(this.apiUrl).subscribe(data => {
      this.productos = data;
    });
  }

  guardar() {
    if (!this.formProducto.categoriaId) {
      alert('Selecciona una categoría válida');
      return;
    }

    if (this.editProducto && this.editProducto.id) {
      // Editar
      this.http.put<Producto>(`${this.apiUrl}/${this.editProducto.id}`, this.formProducto).subscribe(() => {
        this.cargarProductos();
        this.mostrarFormulario = false;
        this.editProducto = null;
        this.formProducto = {
          categoriaId: null,
          codigoInterno: '',
          nombre: '',
          precioVenta: null,
          precioCompra: null,
          moneda: 'Soles',
        };
        this.busquedaCategoriaInput = '';
        this.categoriasFiltradas = this.categorias;
      });
    } else {
      // Nuevo
      this.http.post<Producto>(this.apiUrl, this.formProducto).subscribe(() => {
        this.cargarProductos();
        this.mostrarFormulario = false;
        this.formProducto = {
          categoriaId: null,
          codigoInterno: '',
          nombre: '',
          precioVenta: null,
          precioCompra: null,
          moneda: 'Soles',
        };
        this.busquedaCategoriaInput = '';
        this.categoriasFiltradas = this.categorias;
      });
    }
  }

  eliminarProducto(id: number | undefined) {
    if (!id) return;
    if (confirm('¿Seguro que deseas eliminar este producto?')) {
      this.http.delete(`${this.apiUrl}/${id}`).subscribe(() => this.cargarProductos());
    }
  }

  editar(producto: Producto) {
    this.editProducto = producto;
    this.formProducto = { ...producto };
    const cat = this.categorias.find(c => c.id === producto.categoriaId);
    this.busquedaCategoriaInput = cat ? cat.nombre : '';
    this.categoriasFiltradas = this.categorias;
    this.mostrarFormulario = true;
  }

  nuevoProducto() {
    this.editProducto = null;
    this.formProducto = {
      categoriaId: null,
      codigoInterno: '',
      nombre: '',
      precioVenta: null,
      precioCompra: null,
      moneda: 'Soles',
    };
    this.busquedaCategoriaInput = '';
    this.categoriasFiltradas = this.categorias;
    this.mostrarFormulario = true;
  }

  cancelar() {
    this.mostrarFormulario = false;
    this.editProducto = null;
    this.busquedaCategoriaInput = '';
    this.categoriasFiltradas = this.categorias;
  }

  // AUTOCOMPLETE LUPA
  actualizarCategoriasFiltradas() {
    const texto = this.busquedaCategoriaInput.trim().toLowerCase();
    if (!texto) {
      this.categoriasFiltradas = this.categorias;
      this.formProducto.categoriaId = null;
      return;
    }
    this.categoriasFiltradas = this.categorias.filter(c =>
      c.nombre.toLowerCase().includes(texto)
    );
    // Si escribe exactamente igual a una categoría, la selecciona
    const exacta = this.categorias.find(c => c.nombre.toLowerCase() === texto);
    if (exacta) {
      this.seleccionarCategoria(exacta);
    } else {
      this.formProducto.categoriaId = null;
    }
  }

  seleccionarCategoria(cat: Categoria) {
    this.formProducto.categoriaId = cat.id;
    this.busquedaCategoriaInput = cat.nombre;
    this.categoriasFiltradas = [];
  }

  // Mostrar nombre de categoría en tabla
  getNombreCategoria(id: number | null): string {
    const cat = this.categorias.find(c => c.id === id);
    return cat ? cat.nombre : '';
  }

  // Filtro de productos por categoría y por nombre/código en la tabla
  get productosFiltrados(): Producto[] {
    let productos = this.productos;

    // Filtro por categoría (si hay texto)
    if (this.busquedaCategoria.trim()) {
      productos = productos.filter(p =>
        this.getNombreCategoria(p.categoriaId).toLowerCase().includes(this.busquedaCategoria.trim().toLowerCase())
      );
    }

    // Filtro por producto/código (si hay texto)
    if (this.busquedaProducto.trim()) {
      const texto = this.busquedaProducto.trim().toLowerCase();
      productos = productos.filter(p =>
        (p.nombre && p.nombre.toLowerCase().includes(texto)) ||
        (p.codigoInterno && p.codigoInterno.toLowerCase().includes(texto))
      );
    }

    return productos;
  }
}
