#include "Query.hpp"

#include <curlpp/cURLpp.hpp>
#include <curlpp/Easy.hpp>
#include <curlpp/Options.hpp>
#include <curlpp/Exception.hpp>

namespace osm {
namespace query {
struct Query::Impl final {
    Impl(std::string area, const Type type, const Route route, const Output out);
    virtual ~Impl();
    void Send(const WriteToFile write_to_file);
    void Write(curlpp::Easy * handle, char * ptr, std::size_t size, std::size_t nmemb);
private: 
    Impl() = delete;
    Impl(const Impl&) = delete;
    Impl(Impl&&) = delete;
    Impl& operator=(const Impl&) = delete;
    Impl& operator=(Impl&&) = delete;
private:
    unsigned writeRound;
    std::string area_;
    Output output_;
    Type route_;
    Route transport_;
};

std::shared_ptr<Query> Query::Make(std::string area, const osm::query::Type type, const osm::query::Route route, const osm::query::Output out) {
    return std::make_shared<Query>(std::move(area), type, route, out);
}

Query::Impl::Impl(std::string area, const Type type, const Route route, const Output out) : area_(std::move(area)), route_(type), transport_(route), output_(out){}

Query::Impl::~Impl() {}

Query::Query(std::string area, const osm::query::Type type, const osm::query::Route route, const osm::query::Output out)
: pimpl_(new Impl(std::move(area), type, route, out)) {}

Query::~Query() {}

void Query::Send(const WriteToFile write_to_file) {
  if (!pimpl_) throw std::logic_error("Pimpl is null");
  pimpl_->Send(write_to_file);  
}

void Query::Impl::Write(curlpp::Easy * handle, char * ptr, std::size_t size, std::size_t nmemb) {

}


// todo try catch
void Query::Impl::Send(const WriteToFile write_to_file) {
    std::string url = std::string(INTERPRETER_OVERPASS_DATA_URL);
    // overpass QL
    std::string common = "[type="+type_to_string[route_]+"][route="+route_to_string[transport_]+"]";
    url += "[out:" + output_to_string[output_] + "];"; 
    url += "area[name="+area_+"]-%3E.boundaryarea;";
    url += "(node"+common;
    url += "(area.boundaryarea);way"+common;
    url += "(area.boundaryarea);%3E;relation"+common+"(area.boundaryarea);%3E%3E;);out%20meta;";

    std::cout << url << std::endl;
    curlpp::Cleanup cleaner;
    curlpp::Easy request;

    std::cout << curlpp::options::Url(url) << std::endl;

    if(static_cast<bool>(write_to_file))
        int a = 2;

    }

}
}