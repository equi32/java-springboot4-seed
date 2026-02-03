CREATE TABLE products (
	id uuid NOT NULL,
	name varchar(100) NOT NULL,
	description varchar(1000),
	price decimal(10,2) NOT NULL DEFAULT 0,
	stock int4 NOT NULL DEFAULT 0,
	status varchar(20) NOT NULL,
	created_at timestamp,
	updated_at timestamp,
	CONSTRAINT products_pk PRIMARY KEY (id),
	CONSTRAINT products_name UNIQUE (name)
);
