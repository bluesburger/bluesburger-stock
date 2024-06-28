package br.com.bluesburger.stock.application;

import java.security.InvalidAlgorithmParameterException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Ean {
	private static final String GS1 = "55"; // 2 dígitos: país de origem do produto
	private static final String MARCA = "56789"; // 5 dígitos: marca
	
	public static Boolean validate(String ean) {
		String digitoExtraido = ean.substring(ean.length() - 1);
		String eanSemDigitoVerificador = ean.substring(0, ean.length() - 1);
		
		int digitoCalculado = Ean.checkSum(eanSemDigitoVerificador);
		return digitoExtraido.equals(String.valueOf(digitoCalculado));
	}
	
	public static String defineEan(Long codigo) throws Exception {
		String produto = StringUtils.leftPad(String.valueOf(codigo), 5, "0"); // 5 dígitos: produto
		
		String codigoSemEan = GS1 + MARCA + produto;
		
		String digito = Ean.calculaDigEan13(codigoSemEan).orElseThrow();
		return codigoSemEan + digito;
	}

	public static Optional<String> calculaDigEan13(String code) throws Exception {
		if (code.length() < 12) { //check to see if the input is too short
			try {
				throw new InvalidAlgorithmParameterException("That's not a long enough barcode! Give me the rest of the numbers!");
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace(); 
			    return Optional.empty();
			}
		}
		if (code.length() > 12 ) { //check to see if the input is too long
			try{
				throw new InvalidAlgorithmParameterException("That's too long! You only need 12 numbers!");
			} catch (InvalidAlgorithmParameterException e){
				e.printStackTrace(); 
				return Optional.empty();
			}

		}
		int ans = checkSum(code); //pass that input to the checkSum function
		return Optional.of(String.valueOf(ans));
	}
	
	public static int checkSum(String Input) {
		int evens = 0; //initialize evens variable
		int odds = 0; //initialize odds variable
		int checkSum = 0; //initialize the checkSum
		for (int i = 0; i < Input.length(); i++) {
			//check if number is odd or even
			if ((int)Input.charAt(i) % 2 == 0) { // check that the character at position "i" is divisible by 2 which means it's even
				evens += (int)Input.charAt(i);// then add it to the evens
			} else {
				odds += (int)Input.charAt(i); // else add it to the odds
			}
		}
		odds = odds * 3; //multiply odds by three
		int total = odds + evens; //sum odds and evens
		if (total % 10 == 0){ //if total is divisible by ten, special case
			checkSum = 0;//checksum is zero
		} else { //total is not divisible by ten
			checkSum = 10 - (total % 10); //subtract the ones digit from 10 to find the checksum
		}
		return checkSum;
	}
}
