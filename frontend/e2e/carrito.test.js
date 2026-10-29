import { test, expect } from '@playwright/test';


test('Flujo completo: Registro, agregar al carrito, modificar cantidad y eliminar', async ({ page }) => {
  test.slow();

  const email = `carrito_user_${Date.now()}@test.com`;
  const password = 'password123';
  await page.goto('/registro');
  await page.fill('input[type="email"]', email);
  await page.fill('input[type="password"]', password);
  await page.click('button[type="submit"]');
  await expect(page).toHaveURL('/');

  // 2. Navegar al detalle del primer producto
  const primeraTarjeta = page.locator('article.tarjeta').first();
  await expect(primeraTarjeta).toBeVisible();
  await primeraTarjeta.locator('a.enlace-tarjeta').first().click();
  await expect(page).toHaveURL(/\/productos\/\d+$/);
  const botonAgregar = page.locator('button.boton-agregar-grande');
  await expect(botonAgregar).toBeVisible();
  await botonAgregar.click();
  await expect(botonAgregar).toContainText('¡Agregado al carrito!');

  await page.goto('/carrito');
  await expect(page).toHaveURL('/carrito');

  const filaProducto = page.locator('tbody tr').first();
  await expect(filaProducto).toBeVisible();

  await expect(filaProducto.locator('.control-cantidad span')).toHaveText('1');
  const celdaPrecio = filaProducto.locator('.precio-celda');
  await expect(celdaPrecio).toBeVisible();
  const precioInicialText = await celdaPrecio.innerText();

  const botonAumentar = filaProducto.getByRole('button', { name: 'Aumentar cantidad' });
  await botonAumentar.click();
  await expect(filaProducto.locator('.control-cantidad span')).toHaveText('2');
  await expect(celdaPrecio).not.toHaveText(precioInicialText);

  const botonEliminar = filaProducto.locator('button.boton-eliminar');
  await botonEliminar.click();

  await expect(filaProducto).not.toBeVisible();
});