package geopositions.api;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import geopositions.model.GeoPosition;
import geopositions.services.DaoServices;
import geopositions.services.impl.DaoServicesImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;

public class MainVerticle extends AbstractVerticle {

	private DaoServices daoServices;
	private String contentClient;
	private final Logger log = LoggerFactory.getLogger( MainVerticle.class );
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		
		daoServices= new DaoServicesImpl();
		log.info("Chargement du fichier index.html");
		contentClient=IOUtils.toString((getClass().getResourceAsStream("/index.html")),
				Charset.forName("UTF-8"));
		log.info("Fin du chargement du fichier index.html");
		final Router router = Router.router(vertx);

		
		
		router.route()
				.handler(CorsHandler.create("*").allowedMethod(io.vertx.core.http.HttpMethod.GET)
						.allowedHeader("Access-Control-Request-Method")
						.allowedHeader("Access-Control-Allow-Credentials").allowedHeader("Access-Control-Allow-Origin")
						.allowedHeader("Access-Control-Allow-Headers").allowedHeader("Content-Type"));

		router.post("/positions/addnow/:lattitude/:longitude").handler(this::createPos);
		router.post("/positions/addnow/:lattitude/:longitude/:date").handler(this::createPosWithDate);
		router.get("/positions/").handler(this::getAll);
		router.get("/").handler(this::generateHtml);
		router.get("/positions/year/:year").handler(this::getPositionsByYear);
		router.get("/positions/month/:year/:month").handler(this::getPositionsByMonth);
		router.get("/positions/near/:lattitude/:longitude").handler(this::getPositionsByPosition);

		vertx.createHttpServer().requestHandler(router).listen(8899, res -> {
			if (res.succeeded()) {
				log.info("Serveur demarre sur le port " + 8899);
				startPromise.complete();
			} else {
				startPromise.fail(res.cause());
			}
		});

	}

	private void createPos(RoutingContext ctx) {
		GeoPosition pos  = new GeoPosition();
		pos.setLattitude(Double.parseDouble(ctx.request().getParam("lattitude")));
		pos.setLongitude(Double.parseDouble(ctx.request().getParam("longitude")));
		pos.setDate(dateFormatter.format(new Date().getTime()));
		try {
			log.info("Insertion de "+pos.toString());
			daoServices.insert(pos);
			outputOk(ctx);
		} catch (Exception e) {
			outputError(ctx, e);
		}

	}
	
	private void createPosWithDate(RoutingContext ctx) {
		GeoPosition pos  = new GeoPosition();
		pos.setLattitude(Double.parseDouble(ctx.request().getParam("lattitude")));
		pos.setLongitude(Double.parseDouble(ctx.request().getParam("longitude")));
		pos.setDate(dateFormatter.format(new Date(Long.parseLong(ctx.request().getParam("date")))));
		try {
			log.info("Insertion de "+pos.toString());
			daoServices.insert(pos);
			outputOk(ctx);
		} catch (Exception e) {
			outputError(ctx, e);
		}

	}
	
	private void getAll(RoutingContext ctx) {
		List<GeoPosition> all = daoServices.getAll();
		getJSONResponse(ctx).end(new JsonObject().put("result", all).encodePrettily());
	}

	
	private void getPositionsByYear(RoutingContext ctx) {
		List<GeoPosition> positionsByYear = daoServices.getPositionsByYear(Integer.parseInt(ctx.request().getParam("year")));
		getJSONResponse(ctx).end(new JsonObject().put("result", positionsByYear).encodePrettily());

	}
	private void getPositionsByMonth(RoutingContext ctx) {
		List<GeoPosition> positionsByMonth = daoServices.getPositionsByMonth(Integer.parseInt(ctx.request().getParam("year")),Integer.parseInt(ctx.request().getParam("month")));
		getJSONResponse(ctx).end(new JsonObject().put("result", positionsByMonth).encodePrettily());

	}
	private void getPositionsByPosition(RoutingContext ctx) {
		List<GeoPosition> positionsByPosition = daoServices.getPositionsByPosition(Double.parseDouble(ctx.request().getParam("lattitude")), Double.parseDouble(ctx.request().getParam("longitude")));
		getJSONResponse(ctx).end(new JsonObject().put("result", positionsByPosition).encodePrettily());

	}
	
	
	
	private void outputError(RoutingContext ctx, Exception e) {
		log.error("Erreur : ", e);
		e.printStackTrace();
		getJSONResponse(ctx)
				.end(new JsonObject().put("status", "ko").put("msg", e.getMessage()).encodePrettily());
	}
	
	private void outputOk(RoutingContext ctx) {
		getJSONResponse(ctx).end(new JsonObject().put("status", "ok").encodePrettily());
	}
	

	private HttpServerResponse getJSONResponse(RoutingContext ctx) {
		return ctx.response().putHeader("Content-Type", "application/json");
	}
	
	private void generateHtml(RoutingContext ctx) {
		ctx.response().putHeader("content-type", "text/html").end(contentClient);
	}

}
