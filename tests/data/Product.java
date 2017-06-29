package data;

import cli.annotations.InternalFile;
import cli.annotations.Parameter;

public final class Product
{
	@Parameter(name = "currency")

	private char currency = '$';


	@Parameter(name = "name", required = true)

	private String name = "";


	@InternalFile("files/data/gencodes.txt")

	@Parameter(name = "gencode")

	private int gencode = 4953323;


	@Parameter(name = "price", required = true)

	private float price;
}
