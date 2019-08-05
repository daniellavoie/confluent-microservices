import { Wallet } from './wallet';

describe('Wallet', () => {
  it('should create an instance', () => {
    expect(new Wallet()).toBeTruthy();
  });
});
