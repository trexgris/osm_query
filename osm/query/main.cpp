#include "Query.hpp"

int main() {
    auto ptr = std::make_shared<osm::query::Query>("r",osm::query::Type::ROUTE, osm::query::Route::BUS,osm::query::Output::JSON);
    return 0;
}
