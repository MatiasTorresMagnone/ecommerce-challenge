export const formatoPrecio = (precio) =>
  new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(Number(precio));