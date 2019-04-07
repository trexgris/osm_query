#include "Query.hpp"

namespace osm {
namespace query {
struct Query::Impl final {

    Impl(std::string area, const Type type, const Route route, const Output out);
    virtual ~Impl();
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

}
}