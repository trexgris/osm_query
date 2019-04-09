#include "Query.hpp"
#include <iostream>
#include <curlpp/cURLpp.hpp>
#include <curlpp/Easy.hpp>
#include <curlpp/Options.hpp>
#include <curlpp/Exception.hpp>

int main() {

    try{
        auto ptr = std::make_shared<osm::query::Query>("Nicaragua",osm::query::Type::ROUTE, osm::query::Route::BUS,osm::query::Output::GEO_JSON);
        ptr->Send(osm::query::WriteToFile::YES);
        return 0;
    }
    catch ( const std::exception& e ) {
        std::cout << e.what() << std::endl;
    }
    catch ( const curlpp::LogicError & e )
	{
		std::cout << e.what() << std::endl;
	}
	catch ( const curlpp::RuntimeError & e )
	{
		std::cout << e.what() << std::endl;
    }

}
