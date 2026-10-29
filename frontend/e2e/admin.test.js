import { test, expect } from '@playwright/test';


test('Intento de acceso a /admin sin iniciar sesion debe redirigir', async ({ page }) => {
  await page.goto('/admin');
  await expect(page).not.toHaveURL('/admin');
});

test('Flujo completo de administración: Login admin, creación y alternancia de estado de producto', async ({ page }) => {
  test.slow();
  const nombreProducto = `Teclado Custom Test ${Date.now()}`;

  await page.goto('/login');
  await page.fill('input[type="email"]', 'admin@ecommerce.com');
  await page.fill('input[type="password"]', 'admin123');
  await page.click('button[type="submit"]');
  await expect(page).toHaveURL('/');
  await page.goto('/admin');
  await expect(page).toHaveURL('/admin');

  const botonNuevo = page.getByRole('button', { name: '+ Nuevo producto' });
  await expect(botonNuevo).toBeVisible();
  await botonNuevo.click();

  await page.fill('input[name="nombre"], input[placeholder*="Nombre"]', nombreProducto);
  await page.fill('input[name="precio"], input[type="number"]', '150');
  await page.fill('textarea[name="descripcion"]', 'Descripción de prueba para automatización E2E');
  await page.click('button[type="submit"]');

  const filaProducto = page.locator('tr, .tarjeta-admin').filter({ hasText: nombreProducto });
  await expect(filaProducto).toBeVisible();

  const celdaEstado = filaProducto.locator('.estado-producto');
  await expect(celdaEstado).toHaveText('Activo');

  const botonAlternar = filaProducto.locator('button.boton-borrar');
  await expect(botonAlternar).toHaveText('Desactivar');

  await botonAlternar.click();
  await expect(celdaEstado).toHaveText('Inactivo');
});