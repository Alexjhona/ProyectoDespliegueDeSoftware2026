import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

interface Cliente {
  id: number;
  razonSocialONombre: string;
  dniOrRuc: string;
  direccion?: string;
  telefono?: string;
}
interface Producto {
  id: number;
  nombre: string;
  codigoInterno: string;
  precioVenta: number;
}
interface ItemVenta {
  productoId: number | null;
  cantidad: number;
  precio: number;
  productoNombre?: string;
  busquedaProducto?: string;
  productosFiltrados?: Producto[];
  productoSeleccionado?: Producto;
}
interface Venta {
  id?: number;
  clienteId: number | null;
  clienteNombre?: string;
  fecha?: string;
  total?: number;
  items: ItemVenta[];
}

@Component({
  selector: 'app-venta',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './venta.component.html',
  styleUrl: './venta.component.css',
})
export class VentaComponent {
  ventas: Venta[] = [];
  clientes: Cliente[] = [];
  productos: Producto[] = [];
  nuevaVenta: Venta = {
    clienteId: null,
    items: []
  };
  mensaje: string = '';
  mostrarFormulario = false;

  // Cliente autocompletado
  busquedaCliente: string = '';
  clientesFiltrados: Cliente[] = [];
  clienteSeleccionado: Cliente | null = null;
  mostrarNuevoCliente = false;
  nuevoCliente: Partial<Cliente> = {
    dniOrRuc: '',
    razonSocialONombre: '',
    direccion: '',
    telefono: ''
  };

  // Filtros de ventas
  busqueda: string = '';
  filtroFecha: string = '';
  mostrarDetalles: { [key: number]: boolean } = {};

  private apiVentas = 'http://localhost:8080/api/ventas';
  private apiClientes = 'http://localhost:8080/api/clientes';
  private apiProductos = 'http://localhost:8080/api/productos';

  constructor(private http: HttpClient) {
    this.cargarVentas();
    this.cargarClientes();
    this.cargarProductos();
  }

  // CLIENTES
  cargarClientes() {
    this.http.get<Cliente[]>(this.apiClientes).subscribe(data => {
      this.clientes = data;
    });
  }
  filtrarClientes() {
    const texto = this.busquedaCliente.trim().toLowerCase();
    this.clientesFiltrados = !texto
      ? []
      : this.clientes.filter(c =>
        c.razonSocialONombre.toLowerCase().includes(texto) ||
        (c.dniOrRuc && c.dniOrRuc.includes(texto))
      );
  }
  seleccionarCliente(cli: Cliente) {
    this.clienteSeleccionado = cli;
    this.nuevaVenta.clienteId = cli.id;
    this.busquedaCliente = cli.razonSocialONombre;
    this.clientesFiltrados = [];
  }
  crearCliente() {
    if (!this.nuevoCliente.dniOrRuc || !this.nuevoCliente.razonSocialONombre) {
      alert('Completa DNI/RUC y nombre');
      return;
    }
    this.http.post<Cliente>(this.apiClientes, this.nuevoCliente).subscribe(nuevo => {
      this.cargarClientes();
      this.clienteSeleccionado = nuevo;
      this.nuevaVenta.clienteId = nuevo.id;
      this.busquedaCliente = nuevo.razonSocialONombre;
      this.mostrarNuevoCliente = false;
      this.nuevoCliente = { dniOrRuc: '', razonSocialONombre: '', direccion: '', telefono: '' };
    });
  }

  // PRODUCTOS
  cargarProductos() {
    this.http.get<Producto[]>(this.apiProductos).subscribe(data => {
      this.productos = data;
    });
  }
  filtrarProductos(i: number) {
    const texto = this.nuevaVenta.items[i].busquedaProducto?.trim().toLowerCase() || '';
    this.nuevaVenta.items[i].productosFiltrados = !texto
      ? []
      : this.productos.filter(p =>
        p.nombre.toLowerCase().includes(texto) ||
        (p.codigoInterno && p.codigoInterno.toLowerCase().includes(texto))
      );
  }
  seleccionarProducto(i: number, prod: Producto) {
    this.nuevaVenta.items[i].productoId = prod.id;
    this.nuevaVenta.items[i].productoNombre = prod.nombre;
    this.nuevaVenta.items[i].precio = prod.precioVenta;
    this.nuevaVenta.items[i].productoSeleccionado = prod;
    this.nuevaVenta.items[i].busquedaProducto = prod.nombre;
    this.nuevaVenta.items[i].productosFiltrados = [];
    if (this.nuevaVenta.items[i].cantidad < 1) {
      this.nuevaVenta.items[i].cantidad = 1;
    }
  }
  onCantidadChange(i: number) {
    const item = this.nuevaVenta.items[i];
    if (item.cantidad < 1) {
      item.cantidad = 1;
    }
  }
  agregarItem() {
    this.nuevaVenta.items.push({
      productoId: null,
      cantidad: 1,
      precio: 0,
      busquedaProducto: '',
      productosFiltrados: [],
      productoSeleccionado: undefined
    });
  }
  eliminarItem(i: number) {
    this.nuevaVenta.items.splice(i, 1);
  }

  // VENTAS
  cargarVentas() {
    this.http.get<Venta[]>(this.apiVentas).subscribe(data => {
      // PARCHE: si falta el nombre o precio en los items, buscarlos en el catálogo
      data.forEach(venta => {
        venta.items.forEach(item => {
          if (!item.productoNombre || !item.precio) {
            const prod = this.productos.find(p => p.id === item.productoId);
            item.productoNombre = prod?.nombre || '';
            item.precio = prod?.precioVenta || 0;
          }
        });
      });
      this.ventas = data;
    });
  }

  toggleDetalles(id: number) {
    this.mostrarDetalles[id] = !this.mostrarDetalles[id];
  }
  get totalVenta(): number {
    return this.nuevaVenta.items.reduce((acc, item) => acc + (item.precio * item.cantidad), 0);
  }
  registrarVenta() {
    if (!this.nuevaVenta.clienteId || this.nuevaVenta.items.length === 0) {
      this.mensaje = 'Completa los datos de cliente y al menos un producto';
      return;
    }
    const venta = {
      clienteId: this.nuevaVenta.clienteId,
      items: this.nuevaVenta.items.map(i => ({
        productoId: i.productoId,
        cantidad: i.cantidad,
      })),
    };
    this.http.post<Venta>(this.apiVentas, venta).subscribe({
      next: () => {
        this.mensaje = 'Venta registrada correctamente';
        this.cargarVentas();
        this.nuevaVenta = { clienteId: null, items: [] };
        this.clienteSeleccionado = null;
        this.busquedaCliente = '';
        this.mostrarFormulario = false;
        setTimeout(() => this.mensaje = '', 3000);
      },
      error: () => this.mensaje = 'Error al registrar venta'
    });
  }

  // FILTROS
  get ventasFiltradas(): Venta[] {
    let arr = this.ventas;
    const texto = this.busqueda.trim().toLowerCase();
    if (texto) {
      arr = arr.filter(v =>
        (v.clienteNombre?.toLowerCase().includes(texto) ?? false) ||
        (v.id?.toString().includes(texto))
      );
    }
    if (this.filtroFecha) {
      arr = arr.filter(v => v.fecha?.slice(0, 10) === this.filtroFecha);
    }
    return arr;
  }
  get totalVentasFiltradas(): number {
    return this.ventasFiltradas.reduce((acc, v) => acc + (v.total || 0), 0);
  }
}
