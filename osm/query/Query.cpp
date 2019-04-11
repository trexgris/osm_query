#include "Query.hpp"
#include "writer/Writer.hpp"
#include <curlpp/cURLpp.hpp>
#include <curlpp/Easy.hpp>
#include <curlpp/Options.hpp>
#include <curlpp/Exception.hpp>
#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <cerrno>


namespace osm {
namespace query {
struct Query::Impl final {
    static constexpr const char * const OUTPUT_FILE = "osm_response.xml";
    Impl(std::string area, const Type type, const Route route, const Output out);
    virtual ~Impl();
    void Send(const WriteToFile write_to_file);
private: 
    Impl() = delete;
    Impl(const Impl&) = delete;
    Impl(Impl&&) = delete;
    Impl& operator=(const Impl&) = delete;
    Impl& operator=(Impl&&) = delete;
private:
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

void Query::Impl::Send(const WriteToFile write_to_file) {
    curlpp::Cleanup cleaner;
    curlpp::Easy request;

    std::string url = std::string(INTERPRETER_OVERPASS_DATA_URL);

    // overpass QL
    if(!type_to_string.count(route_) || !route_to_string.count(transport_))
        throw std::logic_error("Entry type not found");
                
    std::string common = "[type="+type_to_string[route_]+"][route="+route_to_string[transport_]+"]";
    url += "[out:" + output_to_string[output_] + "];"; 
    url += "area[name="+area_+"][admin_level=2]-%3E.boundaryarea;";
    url += "(node"+common;
    url += "(area.boundaryarea);way"+common;
    url += "(area.boundaryarea);%3E;relation"+common+"(area.boundaryarea);%3E%3E;);out%20meta;";

    if(static_cast<bool>(write_to_file)) {
        curlpp::options::WriteFunctionCurlFunction callback(writer::Callback); // c style, can improve
        FILE *file = stdout;
        if(OUTPUT_FILE != nullptr) {
            file = fopen(OUTPUT_FILE, "wb");
            if(file == nullptr) {
                throw std::runtime_error("File is null");
            }
        }
        curlpp::OptionTrait<void *, CURLOPT_WRITEDATA> data(file);        
        request.setOpt(callback);
        request.setOpt(data);
    }
	request.setOpt(new curlpp::options::Url(url));
	request.setOpt(new curlpp::options::Verbose(true));
	request.perform();
}

}
}
