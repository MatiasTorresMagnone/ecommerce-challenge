import { test, expect } from '@playwright/test';

test.describe.configure({ mode: 'serial' });


/**
 * Se utiliza un email dinámico.
 * Dado que el backend no expone un endpoint de eliminación de usuarios (y no se justifica
 * agregar endpoints exclusivos para testing en producción), esta se hizo para asegura que las
 * pruebas sean autónomas sin depender de un hook de limpieza (tearDown/delete).
 */

const email = `test_${Date.now()}@test.com`;
const password = 'password123';

test('registro exitoso', async ({ page }) => {
  await page.goto('/registro');
  await page.fill('input[type="email"]', email);
  await page.fill('input[type="password"]', password);
  await page.click('button[type="submit"]');
  await expect(page).toHaveURL('/');
});

test('registro fallido por usuario duplicado', async ({ page }) => {
  await page.goto('/registro');
  await page.fill('input[type="email"]', email);
  await page.fill('input[type="password"]', password);
  await page.click('button[type="submit"]');
  await expect(page.locator('.error')).toBeVisible();
  await expect(page).toHaveURL('/registro');
});

test('register mail formato incorrecto', async ({ page }) => {
  test.slow();
  await page.goto('/registro');
  const emailInput = page.locator('input[type="email"]');
  await emailInput.fill('email');
  await page.fill('input[type="password"]', 'testingggg');
  await page.click('button[type="submit"]');
  const validationMessage = await emailInput.evaluate((input) => input.validationMessage);
  expect(validationMessage).toContain('@');
  await expect(page).toHaveURL('/registro');
});

test('register password formato menos de 8 caracteres', async ({ page }) => {
  test.slow();
  await page.goto('/registro');
  await page.fill('input[type="email"]', 'email@gmail.com');
  const passwordInput = page.locator('input[type="password"]');
  await passwordInput.fill('d');
  await page.click('button[type="submit"]');
  const validationMessage = await passwordInput.evaluate((input) => input.validationMessage);
  expect(validationMessage).toContain('8');
  await expect(page).toHaveURL('/registro');
});

test('login exitoso', async ({ page }) => {
  test.slow();
  await page.goto('/login');
  await page.fill('input[type="email"]', email);
  await page.fill('input[type="password"]', password);
  await page.click('button[type="submit"]');
  await expect(page).toHaveURL('/');
});

test('login email inexistente', async ({ page }) => {
  test.slow();
  await page.goto('/login');
  await page.fill('input[type="email"]', 'mailnoexistetesting@gmail.com');
  await page.fill('input[type="password"]', 'testingggg');
  await page.click('button[type="submit"]');
  await expect(page.locator('.error')).toBeVisible();
  await expect(page).toHaveURL('/login');
});

test('login password incorrecta', async ({ page }) => {
  test.slow();
  await page.goto('/login');
  await page.fill('input[type="email"]', email);
  await page.fill('input[type="password"]', 'testingggg');
  await page.click('button[type="submit"]');
  await expect(page.locator('.error')).toBeVisible();
  await expect(page).toHaveURL('/login');
});

test('login mail formato incorrecto', async ({ page }) => {
  test.slow();
  await page.goto('/login');
  const emailInput = page.locator('input[type="email"]');
  await emailInput.fill('email');
  await page.fill('input[type="password"]', 'testingggg');
  await page.click('button[type="submit"]');
  const validationMessage = await emailInput.evaluate((input) => input.validationMessage);
  expect(validationMessage).toContain('@');
  await expect(page).toHaveURL('/login');
});

test('Cierre de sesion', async ({ page }) => {
  test.slow();
  await page.goto('/login');
  await page.fill('input[type="email"]', email);
  await page.fill('input[type="password"]', password);
  await page.click('button[type="submit"]');
  await expect(page).toHaveURL('/');
  await page.getByRole('button', { name: 'Salir' }).click();
  await expect(page).toHaveURL('/');
  await expect(page.getByRole('link', { name: 'Ingresar' })).toBeVisible();
});




