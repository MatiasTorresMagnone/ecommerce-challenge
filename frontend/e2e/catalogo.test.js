import { test, expect } from '@playwright/test';

//Asumimos que siempre habra al menos un producto
test('Carga de tarjetas de productos', async ({ page }) => {
    await page.goto('/');
    const tarjetas = page.locator('article.tarjeta');
    await expect(tarjetas.first()).toBeVisible();

    const cantidad = await tarjetas.count();
    expect(cantidad).toBeGreaterThan(0);
});

test('Click en una tarjeta y navegación al detalle del producto (/productos/:id)', async ({ page }) => {
    await page.goto('/');
    const primeraTarjeta = page.locator('article.tarjeta');
    await expect(primeraTarjeta.first()).toBeVisible();
    await primeraTarjeta.locator('a.enlace-tarjeta').first().click();
    await expect(page).toHaveURL(/\/productos\/\d+$/);
});
