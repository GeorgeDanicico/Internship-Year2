export interface IRegisterInputs {
  username: string,
  email: string,
  password: string,
  firstName: string,
  lastName: string,
  personalNumericalCode: string,
}

export interface IFormInputs extends IRegisterInputs{
  confirmPassword: string,
}