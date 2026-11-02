USE CMDB
GO
-- Precondiciones explícitas de los casos legacy de alta con entidad existente.
-- Estos tres registros no están en el dump de la entrega; no cambian las aserciones.
DECLARE @id INT;
INSERT INTO Lugares (Nombre, Activo) VALUES ('Nombre del depatamento', 1);
SET @id = SCOPE_IDENTITY();
INSERT INTO Departamentos (Id, Descripcion) VALUES (@id, 'Descripcion del depatamento');

INSERT INTO Lugares (Nombre, Activo) VALUES ('Nombre del service', 1);
SET @id = SCOPE_IDENTITY();
INSERT INTO Servicios (Id, Descripcion, Direccion)
VALUES (@id, 'Descripcion del service', 'Direccion del service');

DECLARE @usuario INT = (SELECT Id FROM Usuarios WHERE Usuario = 'user1');
INSERT INTO Equipos (Codigo, NumeroDeSerie, Marca, Modelo, PrecioDeCompra, FechaDeAlta,
    Descripcion, MesesGarantia, EsPrestamo, NumeroPrecinto, Estado, IdUsuario, IdLugarPropietario)
VALUES ('FIXTURE-PC-EXISTENTE', 'MXL7242PC1', 'HP', 'Elite Desk 705 G3', 800, '2018-07-23',
    'Descripcion1', 36, 0, 111, 'ALTA', @usuario, 1);
SET @id = SCOPE_IDENTITY();
INSERT INTO PCs (Id, DirIp, NombreHost, Procesador, MemoriaRam, TamanoDisco)
VALUES (@id, '10.120.1.1', 'PC_1', 'AMD A6', 4, 500);
INSERT INTO ItemHistoricoLugar (IdEquipo, IdLugar, Descripcion, Fecha, IdUsuario, Estado)
VALUES (@id, 1, 'Fixture de PC existente para pruebas legacy', '2018-07-23', @usuario, 'ALTA');
GO
