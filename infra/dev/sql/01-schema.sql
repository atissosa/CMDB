-- CMDB development schema. Original SQL bodies unchanged; database lifecycle handled separately.
CREATE TABLE Lugares (
	Id			INT IDENTITY(1,1)	CONSTRAINT PK_Lugares			PRIMARY KEY,
	Nombre		VARCHAR(100)		
									CONSTRAINT CK_Lugares_Nombre	CHECK (Nombre IS NOT NULL),
	Activo		BIT					CONSTRAINT DF_Lugares_Activo	DEFAULT (1),
									CONSTRAINT CK_Lugares_Activo	CHECK (Activo IS NOT NULL)
)

CREATE TABLE Departamentos (
	Id			INT				CONSTRAINT PK_Departamentos				PRIMARY KEY,
								CONSTRAINT FK_Departamentos_Lugares		FOREIGN KEY (Id) REFERENCES Lugares (Id),
								CONSTRAINT CK_Departamentos_IdLugar		CHECK (Id IS NOT NULL),
	Descripcion	VARCHAR(MAX)	CONSTRAINT CK_Departamentos_Descripcion	CHECK (Descripcion IS NOT NULL)
)

CREATE TABLE Subagencias (
	Id		INT		CONSTRAINT PK_Subagencias			PRIMARY KEY,
					CONSTRAINT CK_Subagencias_IdLugar	CHECK (Id IS NOT NULL),
					CONSTRAINT FK_Subagencias_Lugares	FOREIGN KEY (Id) REFERENCES Lugares (Id),
	Numero	BIGINT	CONSTRAINT UK_Subagencias_Numero	UNIQUE,
					CONSTRAINT CK_Subagencias_Numero	CHECK (Numero IS NOT NULL)
)

CREATE TABLE Servicios (
	Id			INT				CONSTRAINT PK_Servicios				PRIMARY KEY,
								CONSTRAINT FK_Servicios_Lugares		FOREIGN KEY (Id) REFERENCES Lugares (Id),
	Descripcion	VARCHAR(MAX)	CONSTRAINT CK_Servicios_Descripcion	CHECK (Descripcion IS NOT NULL),
	Direccion	VARCHAR(150)	CONSTRAINT CK_Servicios_Direccion	CHECK (Direccion IS NOT NULL)
)

GO



  /***************************************************************************************************************/
 /*		SE CREAN TABLAS DE USUARIOS																				*/
/***************************************************************************************************************/

CREATE TABLE Roles (
	Id		INT IDENTITY(1,1)	CONSTRAINT PK_Roles			PRIMARY KEY,
	Nombre	VARCHAR(30)			CONSTRAINT UK_Roles_Nombre	UNIQUE,
								CONSTRAINT CK_Roles_Nombre	CHECK (Nombre IS NOT NULL)
)

CREATE TABLE Usuarios (
	Id				INT IDENTITY(1,1)	CONSTRAINT PK_Usuarios					PRIMARY KEY,
	Usuario			VARCHAR(20)			CONSTRAINT UK_Usuarios_Usuario			UNIQUE (Usuario),
										CONSTRAINT CK_Usuarios_Usuario			CHECK (Usuario IS NOT NULL),
	Contrasena		VARCHAR(20)			CONSTRAINT CK_Usuarios_Contrasena		CHECK (Contrasena IS NOT NULL),
	IdRol			INT					CONSTRAINT FK_Usuarios_Roles			FOREIGN KEY (IdRol) REFERENCES Roles (Id),
										CONSTRAINT CK_Usuarios_IdRol			CHECK (IdRol IS NOT NULL),
	IdDepartamento	INT					CONSTRAINT FK_Usuarios_Departamentos	FOREIGN KEY (IdDepartamento) REFERENCES Departamentos (Id),
										CONSTRAINT CK_Usuarios_IdDepartamento	CHECK (IdDepartamento IS NOT NULL),
	Activo			BIT					CONSTRAINT DF_Usuarios_Activo			DEFAULT (1),
										CONSTRAINT CK_Usuarios_Activo			CHECK (Activo IS NOT NULL),
)

GO



  /***************************************************************************************************************/
 /*		SE CREAN TABLAS DE EQUIPOS																				*/
/***************************************************************************************************************/

CREATE TABLE Equipos (
	Id					INT IDENTITY(1,1)	CONSTRAINT PK_Equipos				PRIMARY KEY,
	Codigo				VARCHAR(50)			CONSTRAINT UK_Equipos_Codigo		UNIQUE,
	NumeroDeSerie		VARCHAR(100)		CONSTRAINT UK_Equipos_NumeroDeSerie	UNIQUE,
	Marca				VARCHAR(150),
	Modelo				VARCHAR(150),
	PrecioDeCompra		DECIMAL(10,2),
	FechaDeAlta			DATE,
	Descripcion			VARCHAR(1000),
	MesesGarantia		INT,
	EsPrestamo			BIT,
	NumeroPrecinto		INT,
	Estado				VARCHAR(50),
	IdUsuario			INT					CONSTRAINT FK_Equipos_Usuarios		FOREIGN KEY (IdUsuario) REFERENCES Usuarios (Id),
	IdLugarPropietario	INT					CONSTRAINT FK_Equipos_Lugares		FOREIGN KEY (IdLugarPropietario) REFERENCES Lugares (Id),
	IdEquipoPrestamo	INT					CONSTRAINT FK_Equipos_Equipos		FOREIGN KEY (IdEquipoPrestamo) REFERENCES Equipos (Id)
)

CREATE TABLE Impresoras (
	Id				INT				CONSTRAINT PK_Impresoras			PRIMARY KEY,
									CONSTRAINT FK_Impresoras_Equipos	FOREIGN KEY (Id) REFERENCES Equipos (Id),
	DirIp			VARCHAR(20),
	Conexion		VARCHAR(20),
	Opcion			VARCHAR(50),
	Multifuncion	BIT,
	NombreHost		VARCHAR(250)
)

CREATE TABLE OtrosEquipos (
	Id		INT			CONSTRAINT PK_OtrosEquipos			PRIMARY KEY,
						CONSTRAINT FK_OtrosEquipos_Equipos	FOREIGN KEY (Id) REFERENCES Equipos (Id),
	Tipo	VARCHAR(50)	CONSTRAINT CK_OtrosEquipos_Tipo		CHECK (Tipo IS NOT NULL)
)

CREATE TABLE PCs (
	Id			INT				CONSTRAINT PK_PCs			PRIMARY KEY,
								CONSTRAINT FK_PCs_Equipos	FOREIGN KEY (Id) REFERENCES Equipos (Id),
	DirIp		VARCHAR(20),
	NombreHost	VARCHAR(250),
	Procesador	VARCHAR(100),
	MemoriaRam	INT,
	TamanoDisco	INT
)

CREATE TABLE Validadoras (
	Id	INT	CONSTRAINT PK_Validadoras			PRIMARY KEY,
			CONSTRAINT FK_Validadoras_Equipos	FOREIGN KEY (Id) REFERENCES Equipos (Id)
)

CREATE TABLE Programas (
	Id			INT IDENTITY(1,1)	CONSTRAINT PK_Programas			PRIMARY KEY,
	Nombre		VARCHAR(150)		CONSTRAINT UK_Programas_Nombre	UNIQUE,
	Descripcion	VARCHAR(1000)
)

CREATE TABLE ProgramasPC (
	IdPC		INT	CONSTRAINT FK_ProgramasPC_PCs		FOREIGN KEY (IdPC)			REFERENCES PCs (Id),
	IdPrograma	INT	CONSTRAINT FK_ProgramasPC_Programas	FOREIGN KEY (IdPrograma)	REFERENCES Programas (Id),
					CONSTRAINT PK_ProgramasPC			PRIMARY KEY (IdPC, IdPrograma)
)

CREATE TABLE ItemHistoricoLugar (
	Id					INT IDENTITY(1,1)	CONSTRAINT PK_ItemHistoricoLugar					PRIMARY KEY,
	IdEquipo			INT					CONSTRAINT FK_ItemHistoricoLugar_Equipos			FOREIGN KEY (IdEquipo)	REFERENCES Equipos (Id),
	IdLugar				INT					CONSTRAINT FK_ItemHistoricoLugar_Lugares			FOREIGN KEY (IdLugar)	REFERENCES Lugares (Id),
	Descripcion			VARCHAR(2000),
	Fecha				DATETIME,
	IdUsuario			INT					CONSTRAINT FK_ItemHistoricoLugar_Usuarios			FOREIGN KEY (IdUsuario)	REFERENCES Usuarios (Id),
	Estado				VARCHAR(50),
	IdEquipoPrestamo	INT					CONSTRAINT FK_ItemHistoricoLugar_Equipos_Prestamo	FOREIGN KEY (IdEquipoPrestamo) REFERENCES Equipos (Id)
)

create table PlantillasEquipos(
	Id					INT IDENTITY(1,1)	CONSTRAINT PK_PlantillasEquipos			PRIMARY KEY,
	Marca				VARCHAR(150),
	Modelo				VARCHAR(150),
	PrecioDeCompra		DECIMAL(10,2),
	MesesGarantia		INT,
)

create table PlantillasPCs(
	Id			INT				CONSTRAINT PK_PlantillasPCs	PRIMARY KEY,
								CONSTRAINT FK_PlantillasPCs	FOREIGN KEY (Id) REFERENCES PlantillasEquipos (Id),
	Procesador	VARCHAR(100),
	MemoriaRam	INT,
	TamanoDisco	INT
)

create table PlantillasImpresoras(
	Id				INT				CONSTRAINT PK_PlantillasImpresoras	PRIMARY KEY,
									CONSTRAINT FK_PlantillasImpresoras	FOREIGN KEY (Id) REFERENCES PlantillasEquipos (Id),
	Conexion		VARCHAR(20),
	Opcion			VARCHAR(50),
	Multifuncion	BIT,
)

create table PlantillasValidadoras(
	Id 		INT				CONSTRAINT PK_PlantillasValidadoras			PRIMARY KEY,
							CONSTRAINT FK_PlantillasValidadoras	FOREIGN KEY (Id) REFERENCES PlantillasEquipos (Id),
)

create table PlantillasOtroEquipo(
	Id 		INT				CONSTRAINT PK_PlantillasOtroEquipo			PRIMARY KEY,
							CONSTRAINT FK_PlantillasOtroEquipo	FOREIGN KEY (Id) REFERENCES PlantillasEquipos (Id),
	Tipo	VARCHAR(50)	CONSTRAINT CK_PlantillasOtrosEquipos_Tipo		CHECK (Tipo IS NOT NULL)
)
GO



  /***************************************************************************************************************/
 /*		SE CREAN TABLAS PARA CONTROL DE ACCESO																	*/
/***************************************************************************************************************/

CREATE TABLE Operaciones (
	Nombre	VARCHAR(100) PRIMARY KEY NOT NULL
)

CREATE TABLE Acceso_Roles (
	IdRol		INT				CONSTRAINT FK_Acceso_Roles_Roles		FOREIGN KEY (IdRol) REFERENCES Roles (Id),
	Operacion	VARCHAR(100)	CONSTRAINT CK_Acceso_Roles_Operacion	CHECK (Operacion IS NOT NULL),
								CONSTRAINT FK_Acceso_Roles_Operaciones	FOREIGN KEY (Operacion) REFERENCES Operaciones (Nombre),
								CONSTRAINT PK_Acceso_Roles				PRIMARY KEY (IdRol, Operacion)
)

GO



  /***************************************************************************************************************/
 /*		SE CREAN TABLAS PARA IMPORTACION																		*/
/***************************************************************************************************************/

CREATE TABLE Importar_Subagencias (
	Numero		BIGINT			CONSTRAINT PK_Importar_Subagencias			PRIMARY KEY,
								CONSTRAINT CK_Importar_Subagencias_Numero	CHECK (Numero IS NOT NULL),
	Nombre		VARCHAR(100)	CONSTRAINT CK_Importar_Subagencias_Nombre	CHECK (Nombre IS NOT NULL),
	Activo		BIT				CONSTRAINT DF_Importar_Subagencias_Activo	DEFAULT (1),
								CONSTRAINT CK_Importar_Subagencias_Activo	CHECK (Activo IS NOT NULL)
)
GO

CREATE TRIGGER TG_Importar_Subagencias_INSERT ON Importar_Subagencias INSTEAD OF INSERT
AS

	DECLARE @id int, @nombre varchar(250), @activo bit, @numero numeric

	DECLARE bucle CURSOR FOR
	SELECT nombre, activo, numero FROM inserted

	OPEN bucle

	FETCH NEXT FROM bucle
	INTO @nombre, @activo, @numero

	WHILE @@FETCH_STATUS = 0
	BEGIN
		INSERT INTO Lugares (nombre, activo)
		VALUES(@nombre, @activo)
		SET @id = SCOPE_IDENTITY()

		INSERT INTO Subagencias (id, numero)
		VALUES (@id, @numero)

		FETCH NEXT FROM bucle
		INTO @nombre, @activo, @numero
	END

	CLOSE bucle
	DEALLOCATE bucle
GO



  /***************************************************************************************************************/
 /*		SE CREAN VISTAS																							*/
/***************************************************************************************************************/

CREATE VIEW VUsuarios
AS
	SELECT u.Id, u.Usuario, u.Contrasena, u.Activo, u.IdRol, r.Nombre AS NombreRol, u.IdDepartamento, l.Nombre AS NombreDepartamento, l.Activo AS ActivoDepartamento, d.Descripcion AS DescripcionDepartamento
	FROM		Usuarios u
	INNER JOIN	Roles r			ON r.Id=u.IdRol
	INNER JOIN	Departamentos d	ON d.Id=u.IdDepartamento
	INNER JOIN	Lugares l		ON l.Id=d.Id
GO

CREATE VIEW VDepartamentos
AS
	SELECT l.Id, l.Nombre, d.Descripcion, l.Activo
	FROM		Lugares l
	INNER JOIN	Departamentos d	ON d.Id=l.Id
GO

CREATE VIEW VSubagencias
AS
	SELECT l.Id, l.Nombre, s.Numero, l.Activo
	FROM		Lugares l
	INNER JOIN	Subagencias s	ON s.Id=l.Id
GO

CREATE VIEW VServicios
AS
	SELECT l.Id, l.Nombre, s.Descripcion, s.Direccion, l.Activo
	FROM		Lugares l
	INNER JOIN	Servicios s	ON s.Id=l.Id
GO

CREATE VIEW VEquipos
AS
	SELECT u.Usuario, l.Nombre AS LugarPropietario, e.*, (	SELECT TOP 1 l1.Nombre
															FROM		ItemHistoricoLugar i
															INNER JOIN	Lugares l1 ON l1.Id=i.IdLugar
															WHERE i.IdEquipo=e.Id
															ORDER BY i.Fecha DESC) AS LugarActual
	FROM		Equipos e
	INNER JOIN	Usuarios u	ON u.Id = e.IdUsuario
	INNER JOIN	Lugares l	ON l.Id = e.IdLugarPropietario
GO

CREATE VIEW VImpresoras
AS
	SELECT e.*, i.DirIp, i.Conexion, i.Opcion, i.Multifuncion, i.NombreHost
	FROM		VEquipos e
	INNER JOIN	Impresoras i	ON i.Id=e.Id
GO

CREATE VIEW VOtrosEquipos
AS
	SELECT e.*, o.Tipo
	FROM		VEquipos e
	INNER JOIN	OtrosEquipos o ON o.Id=e.Id
GO

CREATE VIEW VPCs
AS
	SELECT e.*, p.DirIp, p.NombreHost, p.Procesador, p.MemoriaRam, p.TamanoDisco
	FROM		VEquipos e
	INNER JOIN	PCs p		ON p.Id=e.Id
GO

CREATE VIEW VValidadoras
AS
	SELECT e.*
	FROM		VEquipos e
	INNER JOIN	Validadoras v ON v.Id=e.Id
GO

CREATE VIEW VPlantillasPCs
AS
	SELECT        dbo.PlantillasEquipos.*, dbo.PlantillasPCs.Procesador, dbo.PlantillasPCs.MemoriaRam, dbo.PlantillasPCs.TamanoDisco
	FROM          dbo.PlantillasEquipos INNER JOIN
                  dbo.PlantillasPCs ON dbo.PlantillasEquipos.Id = dbo.PlantillasPCs.Id
GO

CREATE VIEW VPlantillasImpresoras
AS
	SELECT        dbo.PlantillasEquipos.*, dbo.PlantillasImpresoras.Conexion, dbo.PlantillasImpresoras.Opcion, dbo.PlantillasImpresoras.Multifuncion
	FROM            dbo.PlantillasEquipos INNER JOIN
                         dbo.PlantillasImpresoras ON dbo.PlantillasEquipos.Id = dbo.PlantillasImpresoras.Id
GO

CREATE VIEW VPlantillasValidadoras
AS
	SELECT        dbo.PlantillasEquipos.*
	FROM            dbo.PlantillasEquipos INNER JOIN
                         dbo.PlantillasValidadoras ON dbo.PlantillasEquipos.Id = dbo.PlantillasValidadoras.Id
GO

CREATE VIEW VPlantillasOtroEquipo
AS
	SELECT        dbo.PlantillasEquipos.*, dbo.PlantillasOtroEquipo.tipo
	FROM            dbo.PlantillasEquipos INNER JOIN
                         dbo.PlantillasOtroEquipo ON dbo.PlantillasEquipos.Id = dbo.PlantillasOtroEquipo.Id
GO

CREATE VIEW VAccesosRoles
AS
	SELECT a.IdRol, r.Nombre AS NombreRol, a.Operacion
	FROM		Acceso_Roles a
	INNER JOIN	Roles r			ON r.Id = a.IdRol
GO



  /***************************************************************************************************************/
 /*		SE CREAN INDICES																						*/
/***************************************************************************************************************/

CREATE INDEX		IX_Lugares_Nombre						ON Lugares (Nombre)

CREATE INDEX		IX_Usuarios_IdRol						ON Usuarios (IdRol)
CREATE INDEX		IX_Usuarios_IdDepartamento				ON Usuarios (IdDepartamento)

CREATE INDEX		IX_Equipos_Marca						ON Equipos (Marca)
CREATE INDEX		IX_Equipos_Modelo						ON Equipos (Modelo)
CREATE INDEX		IX_Equipos_EsPrestamo					ON Equipos (EsPrestamo)
CREATE INDEX		IX_Equipos_Estado						ON Equipos (Estado)
CREATE INDEX		IX_Equipos_IdUsuario					ON Equipos (IdUsuario)
CREATE INDEX		IX_Equipos_IdLugarPropietario			ON Equipos (IdLugarPropietario)
CREATE INDEX		IX_Equipos_IdEquipoPrestamo				ON Equipos (IdEquipoPrestamo)

CREATE INDEX		IX_OtrosEquipos_Tipo					ON OtrosEquipos (Tipo)

CREATE INDEX		IX_ItemHistoricoLugar_IdEquipo			ON ItemHistoricoLugar (IdEquipo)
CREATE INDEX		IX_ItemHistoricoLugar_IdLugar			ON ItemHistoricoLugar (IdLugar)
CREATE INDEX		IX_ItemHistoricoLugar_IdUsuario			ON ItemHistoricoLugar (IdUsuario)
CREATE INDEX		IX_ItemHistoricoLugar_IdEquipoPrestamo	ON ItemHistoricoLugar (IdEquipoPrestamo)
CREATE INDEX		IX_ItemHistoricoLugar_Fecha				ON ItemHistoricoLugar (Fecha)

CREATE UNIQUE INDEX	IX_PlantillasEquipos_MarcaModelo		ON PlantillasEquipos (Marca, Modelo)

CREATE INDEX		IX_AccesoRoles_Operacion				ON Acceso_Roles (Operacion)

GO



  /***************************************************************************************************************/
 /*		NUEVO DEPARTAMENTO																						*/
/***************************************************************************************************************/

CREATE procedure [dbo].[DEPARTAMENTO_CENTRAL_INSERT]
	@id int out,
	@nombre varchar(100),
	@activo bit,
	@descripcion varchar(max)
AS
BEGIN
	INSERT INTO Lugares (Nombre, Activo)
	VALUES (@nombre ,@activo)
	SET @id = SCOPE_IDENTITY();
	
	INSERT INTO Departamentos(Id, Descripcion)
	VALUES (@id, @descripcion);
END
GO



  /***************************************************************************************************************/
 /*		NUEVO SERVICE																							*/
/***************************************************************************************************************/

CREATE procedure [dbo].[SERVICE_INSERT]
	@id int out,
	@nombre varchar(100),
	@activo bit,
	@descripcion varchar(max),
	@direccion varchar(150)
AS
BEGIN
	INSERT INTO Lugares (Nombre, Activo)
	VALUES (@nombre ,@activo)
	SET @id = SCOPE_IDENTITY();
	
	INSERT INTO Servicios(Id, Descripcion, Direccion)
	VALUES (@id, @descripcion ,@direccion);
END
GO



  /***************************************************************************************************************/
 /*		NUEVA IMPRESORA																							*/
/***************************************************************************************************************/

CREATE procedure [dbo].[IMPRESORA_INSERT]
	@Id					INT OUT,
	@Codigo				VARCHAR(50),
	@NumeroDeSerie		VARCHAR(100),
	@Marca				VARCHAR(150),
	@Modelo				VARCHAR(150),
	@PrecioDeCompra		DECIMAL(10,2),
	@FechaDeAlta		DATE,
	@Descripcion		VARCHAR(1000),
	@MesesGarantia		INT,
	@EsPrestamo			BIT,
	@NumeroPrecinto		INT,
	@Estado				VARCHAR(50),
	@IdUsuario			INT,
	@IdLugarPropietario	INT,
	@DirIp				VARCHAR(20),
	@Conexion			VARCHAR(20),
	@Opcion				VARCHAR(50),
	@Multifuncion		BIT,
	@NombreHost			VARCHAR(250)
AS
BEGIN
	INSERT INTO Equipos (Codigo, NumeroDeSerie, Marca, Modelo, PrecioDeCompra, FechaDeAlta, Descripcion, MesesGarantia, EsPrestamo, NumeroPrecinto, Estado, IdUsuario, IdLugarPropietario)
	VALUES (@Codigo, @NumeroDeSerie, @Marca, @Modelo, @PrecioDeCompra, @FechaDeAlta, @Descripcion, @MesesGarantia, @EsPrestamo, @NumeroPrecinto, @Estado, @IdUsuario, @IdLugarPropietario)
	SET @Id = SCOPE_IDENTITY();
	
	INSERT INTO Impresoras(Id, DirIp, Conexion, Opcion, Multifuncion, NombreHost)
	VALUES (@Id, @DirIp, @Conexion, @Opcion, @Multifuncion, @NombreHost);
END
GO



  /***************************************************************************************************************/
 /*		NUEVO EQUIPO (OTROS)																					*/
/***************************************************************************************************************/

CREATE procedure [dbo].[OTRO_EQUIPO_INSERT]
	@Id					INT OUT,
	@Codigo				VARCHAR(50),
	@NumeroDeSerie		VARCHAR(100),
	@Marca				VARCHAR(150),
	@Modelo				VARCHAR(150),
	@PrecioDeCompra		DECIMAL(10,2),
	@FechaDeAlta		DATE,
	@Descripcion		VARCHAR(1000),
	@MesesGarantia		INT,
	@EsPrestamo			BIT,
	@NumeroPrecinto		INT,
	@Estado				VARCHAR(50),
	@IdUsuario			INT,
	@IdLugarPropietario	INT,
	@Tipo				VARCHAR(50)
AS
BEGIN
	INSERT INTO Equipos (Codigo, NumeroDeSerie, Marca, Modelo, PrecioDeCompra, FechaDeAlta, Descripcion, MesesGarantia, EsPrestamo, NumeroPrecinto, Estado, IdUsuario, IdLugarPropietario)
	VALUES (@Codigo, @NumeroDeSerie, @Marca, @Modelo, @PrecioDeCompra, @FechaDeAlta, @Descripcion, @MesesGarantia, @EsPrestamo, @NumeroPrecinto, @Estado, @IdUsuario, @IdLugarPropietario)
	SET @Id = SCOPE_IDENTITY();
	
	INSERT INTO OtrosEquipos(Id, TIpo)
	VALUES (@Id, @Tipo);
END
GO



  /***************************************************************************************************************/
 /*		NUEVO PC																								*/
/***************************************************************************************************************/

CREATE procedure [dbo].[PC_INSERT]
	@Id					INT OUT,
	@Codigo				VARCHAR(50),
	@NumeroDeSerie		VARCHAR(100),
	@Marca				VARCHAR(150),
	@Modelo				VARCHAR(150),
	@PrecioDeCompra		DECIMAL(10,2),
	@FechaDeAlta		DATE,
	@Descripcion		VARCHAR(1000),
	@MesesGarantia		INT,
	@EsPrestamo			BIT,
	@NumeroPrecinto		INT,
	@Estado				VARCHAR(50),
	@IdUsuario			INT,
	@IdLugarPropietario	INT,
	@DirIp				VARCHAR(20),
	@NombreHost			VARCHAR(250),
	@Procesador			VARCHAR(100),
	@MemoriaRam			INT,
	@TamanoDisco		INT
AS
BEGIN
	INSERT INTO Equipos (Codigo, NumeroDeSerie, Marca, Modelo, PrecioDeCompra, FechaDeAlta, Descripcion, MesesGarantia, EsPrestamo, NumeroPrecinto, Estado, IdUsuario, IdLugarPropietario)
	VALUES (@Codigo, @NumeroDeSerie, @Marca, @Modelo, @PrecioDeCompra, @FechaDeAlta, @Descripcion, @MesesGarantia, @EsPrestamo, @NumeroPrecinto, @Estado, @IdUsuario, @IdLugarPropietario)
	SET @Id = SCOPE_IDENTITY();
	
	INSERT INTO PCs(Id, DirIp, NombreHost, Procesador, MemoriaRam, TamanoDisco)
	VALUES (@Id, @DirIp, @NombreHost, @Procesador, @MemoriaRam, @TamanoDisco);
END
GO



  /***************************************************************************************************************/
 /*		NUEVA VALIDADORA																						*/
/***************************************************************************************************************/

CREATE procedure [dbo].[VALIDADORA_INSERT]
	@Id					INT OUT,
	@Codigo				VARCHAR(50),
	@NumeroDeSerie		VARCHAR(100),
	@Marca				VARCHAR(150),
	@Modelo				VARCHAR(150),
	@PrecioDeCompra		DECIMAL(10,2),
	@FechaDeAlta		DATE,
	@Descripcion		VARCHAR(1000),
	@MesesGarantia		INT,
	@EsPrestamo			BIT,
	@NumeroPrecinto		INT,
	@Estado				VARCHAR(50),
	@IdUsuario			INT,
	@IdLugarPropietario	INT
AS
BEGIN
	INSERT INTO Equipos (Codigo, NumeroDeSerie, Marca, Modelo, PrecioDeCompra, FechaDeAlta, Descripcion, MesesGarantia, EsPrestamo, NumeroPrecinto, Estado, IdUsuario, IdLugarPropietario)
	VALUES (@Codigo, @NumeroDeSerie, @Marca, @Modelo, @PrecioDeCompra, @FechaDeAlta, @Descripcion, @MesesGarantia, @EsPrestamo, @NumeroPrecinto, @Estado, @IdUsuario, @IdLugarPropietario)
	SET @Id = SCOPE_IDENTITY();
	
	INSERT INTO Validadoras(Id)
	VALUES (@Id);
END
GO



  /***************************************************************************************************************/
 /*		NUEVO ITEM HISTORICO LUGAR																				*/
/***************************************************************************************************************/

CREATE procedure [dbo].[ITEM_HISTORICO_LUGAR_INSERT]
	@id int out,
	@idEquipo int,
	@idLugar int,
	@descripcion varchar(max),
	@fecha DATETIME,
	@idUsuario int,
	@estado VARCHAR(50),
	@idEquipoPrestamo int
AS
BEGIN
	INSERT INTO ItemHistoricoLugar (IdEquipo, IdLugar, Descripcion, Fecha, IdUsuario, Estado, IdEquipoPrestamo)
	VALUES (@idEquipo, @idLugar, @descripcion, @fecha, @idUsuario, @estado, @idEquipoPrestamo)
	SET @id = SCOPE_IDENTITY();
END
GO


  /***************************************************************************************************************/
 /*		MODIFICAR DEPARTAMENTO																					*/
/***************************************************************************************************************/

CREATE procedure [dbo].[DEPARTAMENTO_CENTRAL_UPDATE]
	@id int,
	@nombre varchar(100),
	@activo bit,
	@descripcion varchar(max)
AS
BEGIN
	UPDATE Lugares
	SET	Nombre = @nombre,
		Activo = @activo
	WHERE Id = @id;
	
	UPDATE Departamentos
	SET Descripcion = @descripcion
	WHERE Id = @id;
END
GO



  /***************************************************************************************************************/
 /*		MODIFICAR SERVICE																						*/
/***************************************************************************************************************/

CREATE procedure [dbo].[SERVICE_UPDATE]
	@id int,
	@nombre varchar(100),
	@activo bit,
	@descripcion varchar(max),
	@direccion varchar(150)
AS
BEGIN
	UPDATE Lugares
	SET	Nombre = @nombre,
		Activo = @activo
	WHERE Id = @id;
	
	UPDATE Servicios
	SET	Descripcion = @descripcion,
		Direccion = @direccion
	WHERE Id = @id;
END
GO



  /***************************************************************************************************************/
 /*		MODIFICAR IMPRESORA																						*/
/***************************************************************************************************************/

CREATE PROCEDURE [dbo].[IMPRESORA_UPDATE]
	@Id					INT,
	@Codigo				VARCHAR(50),
	@NumeroDeSerie		VARCHAR(100),
	@Marca				VARCHAR(150),
	@Modelo				VARCHAR(150),
	@PrecioDeCompra		DECIMAL(10,2),
	@FechaDeAlta		DATE,
	@Descripcion		VARCHAR(1000),
	@MesesGarantia		INT,
	@EsPrestamo			BIT,
	@NumeroPrecinto		INT,
	@Estado				VARCHAR(50),
	@IdUsuario			INT,
	@IdLugarPropietario	INT,
	@DirIp				VARCHAR(20),
	@Conexion			VARCHAR(20),
	@Opcion				VARCHAR(50),
	@Multifuncion		BIT,
	@NombreHost			VARCHAR(250),
	@IdEquipoPrestamo	INT
AS
BEGIN
	UPDATE Equipos
	SET	Codigo = @Codigo,
		NumeroDeSerie = @NumeroDeSerie,
		Marca = @Marca,
		Modelo = @Modelo,
		PrecioDeCompra = @PrecioDeCompra,
		FechaDeAlta = @FechaDeAlta,
		Descripcion = @Descripcion,
		MesesGarantia = @MesesGarantia,
		EsPrestamo = @EsPrestamo,
		NumeroPrecinto = @NumeroPrecinto,
		Estado = @Estado,
		IdUsuario = @IdUsuario,
		IdLugarPropietario = @IdLugarPropietario,
		IdEquipoPrestamo = @IdEquipoPrestamo
	WHERE Id = @Id;

	UPDATE Impresoras
	SET	DirIp = @DirIp,
		Conexion = @Conexion,
		Opcion = @Opcion,
		Multifuncion = @Multifuncion,
		NombreHost = @NombreHost
	WHERE Id = @Id;
END
GO



  /***************************************************************************************************************/
 /*		MODIFICAR EQUIPO (OTROS)																				*/
/***************************************************************************************************************/

CREATE PROCEDURE [dbo].[OTRO_EQUIPO_UPDATE]
	@Id					INT,
	@Codigo				VARCHAR(50),
	@NumeroDeSerie		VARCHAR(100),
	@Marca				VARCHAR(150),
	@Modelo				VARCHAR(150),
	@PrecioDeCompra		DECIMAL(10,2),
	@FechaDeAlta		DATE,
	@Descripcion		VARCHAR(1000),
	@MesesGarantia		INT,
	@EsPrestamo			BIT,
	@NumeroPrecinto		INT,
	@Estado				VARCHAR(50),
	@IdUsuario			INT,
	@IdLugarPropietario	INT,
	@Tipo				VARCHAR(50),
	@IdEquipoPrestamo	INT
AS
BEGIN
	UPDATE Equipos
	SET	Codigo = @Codigo,
		NumeroDeSerie = @NumeroDeSerie,
		Marca = @Marca,
		Modelo = @Modelo,
		PrecioDeCompra = @PrecioDeCompra,
		FechaDeAlta = @FechaDeAlta,
		Descripcion = @Descripcion,
		MesesGarantia = @MesesGarantia,
		EsPrestamo = @EsPrestamo,
		NumeroPrecinto = @NumeroPrecinto,
		Estado = @Estado,
		IdUsuario = @IdUsuario,
		IdLugarPropietario = @IdLugarPropietario,
		IdEquipoPrestamo = @IdEquipoPrestamo
	WHERE Id = @Id;

	UPDATE OtrosEquipos
	SET	Tipo = @Tipo
	WHERE Id = @Id;
END
GO



  /***************************************************************************************************************/
 /*		MODIFICAR PC																							*/
/***************************************************************************************************************/

CREATE PROCEDURE [dbo].[PC_UPDATE]
	@Id					INT,
	@Codigo				VARCHAR(50),
	@NumeroDeSerie		VARCHAR(100),
	@Marca				VARCHAR(150),
	@Modelo				VARCHAR(150),
	@PrecioDeCompra		DECIMAL(10,2),
	@FechaDeAlta		DATE,
	@Descripcion		VARCHAR(1000),
	@MesesGarantia		INT,
	@EsPrestamo			BIT,
	@NumeroPrecinto		INT,
	@Estado				VARCHAR(50),
	@IdUsuario			INT,
	@IdLugarPropietario	INT,
	@DirIp				VARCHAR(20),
	@NombreHost			VARCHAR(250),
	@Procesador			VARCHAR(100),
	@MemoriaRam			INT,
	@TamanoDisco		INT,
	@IdEquipoPrestamo	INT
AS
BEGIN
	UPDATE Equipos
	SET	Codigo = @Codigo,
		NumeroDeSerie = @NumeroDeSerie,
		Marca = @Marca,
		Modelo = @Modelo,
		PrecioDeCompra = @PrecioDeCompra,
		FechaDeAlta = @FechaDeAlta,
		Descripcion = @Descripcion,
		MesesGarantia = @MesesGarantia,
		EsPrestamo = @EsPrestamo,
		NumeroPrecinto = @NumeroPrecinto,
		Estado = @Estado,
		IdUsuario = @IdUsuario,
		IdLugarPropietario = @IdLugarPropietario,
		IdEquipoPrestamo = @IdEquipoPrestamo
	WHERE Id = @Id;

	UPDATE PCs
	SET	DirIp = @DirIp,
		NombreHost = @NombreHost,
		Procesador = @Procesador,
		MemoriaRam = @MemoriaRam,
		TamanoDisco = @TamanoDisco
	WHERE Id = @Id;
END
GO



  /***************************************************************************************************************/
 /*		MODIFICAR VALIDADORA																					*/
/***************************************************************************************************************/

CREATE PROCEDURE [dbo].[VALIDADORA_UPDATE]
	@Id					INT,
	@Codigo				VARCHAR(50),
	@NumeroDeSerie		VARCHAR(100),
	@Marca				VARCHAR(150),
	@Modelo				VARCHAR(150),
	@PrecioDeCompra		DECIMAL(10,2),
	@FechaDeAlta		DATE,
	@Descripcion		VARCHAR(1000),
	@MesesGarantia		INT,
	@EsPrestamo			BIT,
	@NumeroPrecinto		INT,
	@Estado				VARCHAR(50),
	@IdUsuario			INT,
	@IdLugarPropietario	INT,
	@IdEquipoPrestamo	INT
AS
BEGIN
	UPDATE Equipos
	SET	Codigo = @Codigo,
		NumeroDeSerie = @NumeroDeSerie,
		Marca = @Marca,
		Modelo = @Modelo,
		PrecioDeCompra = @PrecioDeCompra,
		FechaDeAlta = @FechaDeAlta,
		Descripcion = @Descripcion,
		MesesGarantia = @MesesGarantia,
		EsPrestamo = @EsPrestamo,
		NumeroPrecinto = @NumeroPrecinto,
		Estado = @Estado,
		IdUsuario = @IdUsuario,
		IdLugarPropietario = @IdLugarPropietario,
		IdEquipoPrestamo = @IdEquipoPrestamo
	WHERE Id = @Id;
END
GO
